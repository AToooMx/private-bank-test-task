package privatebank.demo.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import privatebank.demo.model.Currency;

import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExchangeRateDynamicDto {
    private Currency currency;
    private Instant date;
    private Double dynamicRateBuy;
    private Double dynamicRateSell;
}
