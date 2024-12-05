package privatebank.demo.integration.monobank.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MonoBankExchangeRate {
    private Integer currencyCodeA;
    private Integer currencyCodeB;
    private Integer date;
    private Double rateBuy;
    private Double rateSell;
}
