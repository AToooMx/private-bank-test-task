package privatebank.demo.integration.monobank;

import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;
import privatebank.demo.integration.monobank.dto.MonoBankExchangeRate;

import java.util.List;

@RequiredArgsConstructor
public class MonoBankClientImpl implements MonoBankClient {
    private final WebClient webClient;

    @Override
    public List<MonoBankExchangeRate> getExchangeRates() {
        return webClient.get()
            .uri(uriBuilder -> uriBuilder.path("/currency")
                .build())
            .retrieve()
            .bodyToMono(new ParameterizedTypeReference<List<MonoBankExchangeRate>>() {
            })
            .block();
    }
}
