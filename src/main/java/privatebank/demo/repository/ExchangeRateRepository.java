package privatebank.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import privatebank.demo.model.Currency;
import privatebank.demo.model.ExchangeRate;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface ExchangeRateRepository extends JpaRepository<ExchangeRate, Long> {

    Optional<ExchangeRate> findTopByCurrencyOrderByCreatedAtDesc(Currency currency);

    List<ExchangeRate> findTop2ByCurrencyOrderByCreatedAtDesc(Currency currency);

    List<ExchangeRate> findAllByCreatedAtAfterAndCurrencyOrderByCreatedAt(Instant time, Currency currency);

}
