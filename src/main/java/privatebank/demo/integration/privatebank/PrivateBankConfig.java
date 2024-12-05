package privatebank.demo.integration.privatebank;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class PrivateBankConfig {

    @Bean
    public PrivateBankClient privateBankClient(PrivateBankClientConfigProperties properties,
                                            WebClient.Builder webClientBuilder) {
        var client = webClientBuilder
            .baseUrl(properties.getServiceUrl())
            .build();
        return new PrivateBankClientImpl(client);
    }
}
