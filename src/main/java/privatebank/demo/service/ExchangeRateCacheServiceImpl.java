package privatebank.demo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import privatebank.demo.exception.CustomException;
import privatebank.demo.exception.ServerError;
import privatebank.demo.integration.monobank.MonoBankClient;
import privatebank.demo.integration.monobank.dto.MonoBankExchangeRate;
import privatebank.demo.integration.privatebank.PrivateBankClient;
import privatebank.demo.integration.privatebank.dto.PrivateBankExchangeRate;
import privatebank.demo.messaging.producer.EventPublisher;
import privatebank.demo.model.Currency;
import privatebank.demo.model.ExchangeRate;
import privatebank.demo.repository.ExchangeRateRepository;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExchangeRateCacheServiceImpl implements ExchangeRateCacheService {
    private final EventPublisher eventPublisher;
    private final ExchangeRateRepository exchangeRateRepository;
    private final PrivateBankClient privateBankClient;
    private final MonoBankClient monoBankClient;

    @Override
    public void cacheExchangeRates() {
        try {
            var privateBankApiCallFuture = CompletableFuture.supplyAsync(privateBankClient::getExchangeRates)
                .exceptionally(e -> handleApiError(e, "PrivateBank"));
            var monoBankApiCallFuture = CompletableFuture.supplyAsync(monoBankClient::getExchangeRates)
                .exceptionally(e -> handleApiError(e, "MonoBank"));
            CompletableFuture.allOf(privateBankApiCallFuture, monoBankApiCallFuture).join();
            var privateBankRates = privateBankApiCallFuture.get();
            var monoBankRates = monoBankApiCallFuture.get();
            saveExchangeRate(privateBankRates, monoBankRates, Currency.USD);
            saveExchangeRate(privateBankRates, monoBankRates, Currency.EUR);
        } catch (Exception ex) {
            log.error("cacheExchangeRates() exchange rate caching error, errorMessage={}", ex.getMessage(), ex);
        }
    }

    private <T> T handleApiError(Throwable e, String apiName) {
        log.error("{} API call failed: {}", apiName, e.getMessage());
        throw new CustomException(ServerError.API_CALL_ERROR);
    }

    private void saveExchangeRate(List<PrivateBankExchangeRate> privateBankRates, List<MonoBankExchangeRate> monoBankRates, Currency currency) {
        var privateBankExchangeRate = getExchangeRateByCurrency(privateBankRates, currency);
        var monoBankExchangeRate = getExchangeRateByCurrencyCode(monoBankRates, currency.getCode());
        log.debug("saveExchangeRate(): retrieved rates. privateBank={}, monoBank={}", privateBankExchangeRate, monoBankExchangeRate);
        var averageBuyRate = calculateAverage(privateBankExchangeRate.getBuy(), monoBankExchangeRate.getRateBuy());
        var averageSellRate = calculateAverage(privateBankExchangeRate.getSale(), monoBankExchangeRate.getRateSell());
        log.debug("saveExchangeRate(): calculated averages. buy={}, sell={}", averageBuyRate, averageSellRate);
        var exchangeRate = exchangeRateRepository.save(ExchangeRate.builder()
            .createdAt(Instant.now())
            .averageBuyRate(averageBuyRate)
            .averageSellRate(averageSellRate)
            .currency(currency)
            .build());
        eventPublisher.send(exchangeRate);
        log.info("saveExchangeRate(): saved rate for currency={}", currency);
    }

    private Double calculateAverage(String privateBankRate, Double monoBankRate) {
        try {
            return (Double.parseDouble(privateBankRate) + monoBankRate) / 2;
        } catch (NumberFormatException e) {
            log.error("calculateAverage(): Error parsing rates, privateBankRateBuy={}, monoBankRate={}", privateBankRate, monoBankRate, e);
            throw new CustomException(ServerError.INVALID_RATE_FORMAT);
        }
    }

    private PrivateBankExchangeRate getExchangeRateByCurrency(List<PrivateBankExchangeRate> privateBankRates, Currency currency) {
        return privateBankRates.stream()
            .filter(rate -> currency == Currency.valueOf(rate.getCcy()))
            .findFirst()
            .orElseThrow(() -> {
                log.error("getExchangeRateByCurrency(): not found currency rate by currency, currency={}", currency);
                return new CustomException(ServerError.EXCHANGE_RATE_NOT_FOUND);
            });
    }

    private MonoBankExchangeRate getExchangeRateByCurrencyCode(List<MonoBankExchangeRate> monoBankRates, Integer code) {
        return monoBankRates.stream()
            .filter(rate -> code.equals(rate.getCurrencyCodeA()))
            .findFirst()
            .orElseThrow(() -> {
                log.error("getExchangeRateByCurrencyCode(): not found currency rate by code, currencyCode={}", code);
                return new CustomException(ServerError.EXCHANGE_RATE_NOT_FOUND);
            });
    }
}
