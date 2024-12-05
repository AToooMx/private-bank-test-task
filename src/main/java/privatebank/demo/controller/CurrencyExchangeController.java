package privatebank.demo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import privatebank.demo.dto.ExchangeRateDto;
import privatebank.demo.dto.ExchangeRateDynamicDto;
import privatebank.demo.model.Currency;
import privatebank.demo.service.ExchangeRateService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/exchange/rates")
public class CurrencyExchangeController {
    private final ExchangeRateService exchangeRateService;

    @GetMapping("/latest")
    public ExchangeRateDto getLatestRate(@RequestParam(name = "currency") Currency currency) {
        return exchangeRateService.getLatestRate(currency);
    }

    @GetMapping("/hourly")
    public ExchangeRateDynamicDto getHourlyDynamics(@RequestParam(name = "currency") Currency currency) {
        return exchangeRateService.getHourlyDynamics(currency);
    }

    @GetMapping("/daily")
    public List<ExchangeRateDynamicDto> getDailyDynamics(@RequestParam(name = "currency") Currency currency) {
        return exchangeRateService.getDailyDynamics(currency);
    }
}
