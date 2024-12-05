package privatebank.demo.integration.monobank;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "integration.mono-bank")
public class MonoBankClientConfigProperties {
    private String serviceUrl;
}
