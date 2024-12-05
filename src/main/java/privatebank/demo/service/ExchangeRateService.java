package privatebank.demo.service;

import privatebank.demo.dto.ExchangeRateDto;
import privatebank.demo.dto.ExchangeRateDynamicDto;
import privatebank.demo.model.Currency;

import java.util.List;

public interface ExchangeRateService {
    ExchangeRateDto getLatestRate(Currency currency);

    ExchangeRateDynamicDto getHourlyDynamics(Currency currency);

    List<ExchangeRateDynamicDto> getDailyDynamics(Currency currency);
}
