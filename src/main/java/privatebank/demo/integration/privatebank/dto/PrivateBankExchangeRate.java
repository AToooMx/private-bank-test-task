package privatebank.demo.integration.privatebank.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PrivateBankExchangeRate {
    private String ccy;
    @JsonProperty("base_ccy")
    private String baseCcy;
    private String buy;
    private String sale;
}
