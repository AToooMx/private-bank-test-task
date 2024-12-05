package privatebank.demo.integration.monobank;

import privatebank.demo.integration.monobank.dto.MonoBankExchangeRate;

import java.util.List;

public interface MonoBankClient {
    List<MonoBankExchangeRate> getExchangeRates();
}
