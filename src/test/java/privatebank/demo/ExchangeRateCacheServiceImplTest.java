package privatebank.demo;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import privatebank.demo.exception.CustomException;
import privatebank.demo.exception.ServerError;
import privatebank.demo.integration.monobank.MonoBankClient;
import privatebank.demo.integration.monobank.dto.MonoBankExchangeRate;
import privatebank.demo.integration.privatebank.PrivateBankClient;
import privatebank.demo.integration.privatebank.dto.PrivateBankExchangeRate;
import privatebank.demo.messaging.producer.EventPublisher;
import privatebank.demo.model.ExchangeRate;
import privatebank.demo.repository.ExchangeRateRepository;
import privatebank.demo.service.ExchangeRateCacheServiceImpl;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ExchangeRateCacheServiceImplTest {
    @Mock
    private ExchangeRateRepository exchangeRateRepository;
    @Mock
    private PrivateBankClient privateBankClient;
    @Mock
    private MonoBankClient monoBankClient;
    @Mock
    private EventPublisher eventPublisher;
    @InjectMocks
    private ExchangeRateCacheServiceImpl exchangeRateCacheService;

    @Test
    void сacheCurrencyExchangeWhenApiCallSuccessful() {
        var privateBankRates = List.of(new PrivateBankExchangeRate("USD", "UAH", "41.30000", "41.90000"),
            new PrivateBankExchangeRate("EUR", "UAH", "43.35000", "44.35000"));
        var monoBankRates = List.of(new MonoBankExchangeRate(840, 980, 1733207406, 25.7, 26.2),
            new MonoBankExchangeRate(978, 980, 1733207406, 43.47, 44.1794));
        when(privateBankClient.getExchangeRates()).thenReturn(privateBankRates);
        when(monoBankClient.getExchangeRates()).thenReturn(monoBankRates);
        exchangeRateCacheService.cacheExchangeRates();
        verify(exchangeRateRepository, times(2)).save(any(ExchangeRate.class));
    }

    @Test
    void handleApiErrorWhenApiCallFails() {
        when(privateBankClient.getExchangeRates()).thenThrow(new CustomException(ServerError.API_CALL_ERROR));
        when(monoBankClient.getExchangeRates()).thenReturn(List.of(new MonoBankExchangeRate(840, 980, 1733207406, 25.7, 26.2),
            new MonoBankExchangeRate(978, 980, 1733207406, 43.47, 44.1794)));
        exchangeRateCacheService.cacheExchangeRates();
        verify(exchangeRateRepository, times(0)).save(any(ExchangeRate.class));
    }
}
