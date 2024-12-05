package privatebank.demo.integration.privatebank;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "integration.private-bank")
public class PrivateBankClientConfigProperties {
    private String serviceUrl;
}
