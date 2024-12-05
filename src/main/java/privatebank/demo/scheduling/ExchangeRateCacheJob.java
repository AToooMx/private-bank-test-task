package privatebank.demo.scheduling;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import privatebank.demo.service.ExchangeRateCacheService;

import java.time.Instant;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(value = "scheduling.currency-rate-cache.enabled", havingValue = "true")
public class ExchangeRateCacheJob {
    private final ExchangeRateCacheService exchangeRateCacheService;

    @Scheduled(fixedDelayString = "${scheduling.currency-rate-cache.delay}")
    public void exchangeRateCacheJob() {
        log.info("currencyRateCacheJob(): startTime={}", Instant.now());
        exchangeRateCacheService.cacheExchangeRates();
        log.info("currencyRateCacheJob(): endTime={}", Instant.now());
    }

}
