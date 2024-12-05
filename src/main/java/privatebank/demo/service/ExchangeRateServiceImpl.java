package privatebank.demo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import privatebank.demo.dto.ExchangeRateDto;
import privatebank.demo.dto.ExchangeRateDynamicDto;
import privatebank.demo.exception.CustomException;
import privatebank.demo.exception.ServerError;
import privatebank.demo.mapper.ExchangeMapper;
import privatebank.demo.model.Currency;
import privatebank.demo.repository.ExchangeRateRepository;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import static privatebank.demo.consant.AppConstant.KYIV_TIME_ZONE;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExchangeRateServiceImpl implements ExchangeRateService {
    private final ExchangeMapper exchangeMapper;
    private final ExchangeRateRepository exchangeRateRepository;

    @Override
    @Cacheable("latestRates")
    public ExchangeRateDto getLatestRate(Currency currency) {
        var response = exchangeRateRepository.findTopByCurrencyOrderByCreatedAtDesc(currency)
            .map(exchangeMapper::toDto)
            .orElseGet(ExchangeRateDto::new);
        log.info("getLatestRate(): currency={}, response={}", currency, response);
        return response;
    }

    @Override
    @Cacheable("hourlyDynamics")
    public ExchangeRateDynamicDto getHourlyDynamics(Currency currency) {
        log.info("getHourlyDynamics(): currency={}", currency);
        var rates = exchangeRateRepository.findTop2ByCurrencyOrderByCreatedAtDesc(currency);
        if (rates.size() < 2) {
            log.error("getHourlyDynamics(): insufficient data for currency={}", currency);
            throw new CustomException(ServerError.NO_SUFFICIENT_DATA);
        }
        var currentRate = rates.get(0);
        var previousRate = rates.get(1);
        var response = ExchangeRateDynamicDto.builder()
            .dynamicRateBuy(calculatePercentageChange(currentRate.getAverageBuyRate(), previousRate.getAverageBuyRate()))
            .dynamicRateSell(calculatePercentageChange(currentRate.getAverageSellRate(), previousRate.getAverageSellRate()))
            .build();
        log.info("getHourlyDynamics(): currency={}, response={}", currency, response);
        return response;
    }

    @Override
    @Cacheable("dailyDynamics")
    public List<ExchangeRateDynamicDto> getDailyDynamics(Currency currency) {
        var startOfDay = LocalDate.now().atStartOfDay(ZoneId.of(KYIV_TIME_ZONE)).toInstant();
        log.info("getDailyDynamics(): currency={}, startOfDay={}", currency, startOfDay);
        var rates = exchangeRateRepository.findAllByCreatedAtAfterAndCurrencyOrderByCreatedAt(startOfDay, currency);
        if (rates.size() < 2) {
            log.error("getDailyDynamics(): insufficient data for currency={}", currency);
            throw new CustomException(ServerError.NO_SUFFICIENT_DATA);
        }
        var response = new ArrayList<ExchangeRateDynamicDto>();
        for (int i = 1; i < rates.size(); i++) {
            var currentRate = rates.get(i);
            var previousRate = rates.get(i - 1);
            response.add(ExchangeRateDynamicDto.builder()
                .dynamicRateBuy(calculatePercentageChange(currentRate.getAverageBuyRate(), previousRate.getAverageBuyRate()))
                .dynamicRateSell(calculatePercentageChange(currentRate.getAverageSellRate(), previousRate.getAverageSellRate()))
                .date(currentRate.getCreatedAt())
                .build());
        }
        log.info("getDailyDynamics(): currency={}, responseSize={}", currency, response.size());
        return response;
    }

    private Double calculatePercentageChange(Double currentRate, Double previousRate) {
        if (currentRate == null || previousRate == null || previousRate == 0) {
            log.warn("Invalid rate values: currentRate={}, previousRate={}", currentRate, previousRate);
            throw new CustomException(ServerError.VALIDATION_ERROR);
        }
        return ((currentRate - previousRate) / previousRate) * 100;
    }
}
