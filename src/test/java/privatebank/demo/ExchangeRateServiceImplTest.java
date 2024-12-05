package privatebank.demo;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import privatebank.demo.dto.ExchangeRateDto;
import privatebank.demo.dto.ExchangeRateDynamicDto;
import privatebank.demo.exception.CustomException;
import privatebank.demo.mapper.ExchangeMapper;
import privatebank.demo.model.Currency;
import privatebank.demo.model.ExchangeRate;
import privatebank.demo.repository.ExchangeRateRepository;
import privatebank.demo.service.ExchangeRateServiceImpl;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ExchangeRateServiceImplTest {
    @Mock
    private ExchangeRateRepository exchangeRateRepository;
    @Mock
    private ExchangeMapper exchangeMapper;
    @InjectMocks
    private ExchangeRateServiceImpl exchangeRateService;

    @Test
    void shouldReturnLatestRateWhenDataExists() {
        Currency currency = Currency.USD;
        ExchangeRate entity = new ExchangeRate();
        ExchangeRateDto dto = new ExchangeRateDto();

        when(exchangeRateRepository.findTopByCurrencyOrderByCreatedAtDesc(currency))
            .thenReturn(Optional.of(entity));
        when(exchangeMapper.toDto(entity)).thenReturn(dto);

        ExchangeRateDto result = exchangeRateService.getLatestRate(currency);

        assertEquals(dto, result);
        verify(exchangeRateRepository).findTopByCurrencyOrderByCreatedAtDesc(currency);
        verify(exchangeMapper).toDto(entity);
    }

    @Test
    void getLatestRateShouldReturnEmptyDtoIfNoData() {
        Currency currency = Currency.USD;

        when(exchangeRateRepository.findTopByCurrencyOrderByCreatedAtDesc(currency))
            .thenReturn(Optional.empty());

        ExchangeRateDto result = exchangeRateService.getLatestRate(currency);

        assertNotNull(result);
        verify(exchangeRateRepository).findTopByCurrencyOrderByCreatedAtDesc(currency);
        verifyNoInteractions(exchangeMapper);
    }

    @Test
    void getHourlyDynamicsShouldReturnDynamicsIfEnoughData() {
        Currency currency = Currency.EUR;
        ExchangeRate currentRate = new ExchangeRate();
        ExchangeRate previousRate = new ExchangeRate();
        currentRate.setAverageBuyRate(43.41);
        currentRate.setAverageSellRate(44.26);
        previousRate.setAverageBuyRate(43.51);
        previousRate.setAverageSellRate(44.36);

        when(exchangeRateRepository.findTop2ByCurrencyOrderByCreatedAtDesc(currency))
            .thenReturn(List.of(currentRate, previousRate));

        ExchangeRateDynamicDto result = exchangeRateService.getHourlyDynamics(currency);

        assertNotNull(result);
        assertEquals(-0.229, result.getDynamicRateBuy(), 0.01);
        assertEquals(-0.225, result.getDynamicRateSell(), 0.01);
        verify(exchangeRateRepository).findTop2ByCurrencyOrderByCreatedAtDesc(currency);
    }

    @Test
    void getHourlyDynamicsShouldThrowExceptionIfNotEnoughData() {
        Currency currency = Currency.USD;

        when(exchangeRateRepository.findTop2ByCurrencyOrderByCreatedAtDesc(currency))
            .thenReturn(List.of(new ExchangeRate()));

        assertThrows(CustomException.class, () -> exchangeRateService.getHourlyDynamics(currency));
        verify(exchangeRateRepository).findTop2ByCurrencyOrderByCreatedAtDesc(currency);
    }

    @Test
    void getDailyDynamics_shouldReturnDailyDynamics() {
        Currency currency = Currency.USD;
        ExchangeRate rate1 = new ExchangeRate();
        ExchangeRate rate2 = new ExchangeRate();
        rate1.setAverageBuyRate(43.51);
        rate1.setAverageSellRate(44.36);
        rate1.setCreatedAt(Instant.now());
        rate2.setAverageBuyRate(43.41);
        rate2.setAverageSellRate(44.26);
        rate2.setCreatedAt(Instant.now().minus(1, ChronoUnit.HOURS));


        when(exchangeRateRepository.findAllByCreatedAtAfterAndCurrencyOrderByCreatedAt(
            any(Instant.class), eq(currency)))
            .thenReturn(List.of(rate1, rate2));

        List<ExchangeRateDynamicDto> result = exchangeRateService.getDailyDynamics(currency);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(-0.229, result.get(0).getDynamicRateBuy(), 0.01);
        assertEquals(-0.225, result.get(0).getDynamicRateSell(), 0.01);
        verify(exchangeRateRepository).findAllByCreatedAtAfterAndCurrencyOrderByCreatedAt(any(Instant.class), eq(currency));
    }
}
