package privatebank.demo.integration.privatebank;

import privatebank.demo.integration.privatebank.dto.PrivateBankExchangeRate;

import java.util.List;

public interface PrivateBankClient {

    List<PrivateBankExchangeRate> getExchangeRates();

}
