package privatebank.demo.messaging.config;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
@ConfigurationProperties(prefix = "rabbitmq")
public class RabbitMqConfigProperties {
    @NotNull
    private RabbitMq exchangeRates;

    @Data
    public static class RabbitMq {
        @NotNull
        private String exchange;
        @NotNull
        private String routingKey;
    }
}
