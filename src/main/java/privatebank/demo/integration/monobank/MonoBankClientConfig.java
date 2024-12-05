package privatebank.demo.integration.monobank;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class MonoBankClientConfig {

    @Bean
    public MonoBankClient monoBankClient(MonoBankClientConfigProperties properties,
                                         WebClient.Builder webClientBuilder) {
        var client = webClientBuilder
            .baseUrl(properties.getServiceUrl())
            .build();
        return new MonoBankClientImpl(client);
    }
}
