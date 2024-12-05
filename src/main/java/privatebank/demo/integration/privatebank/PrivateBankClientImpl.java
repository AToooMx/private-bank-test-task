package privatebank.demo.integration.privatebank;

import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;
import privatebank.demo.integration.privatebank.dto.PrivateBankExchangeRate;

import java.util.List;

@RequiredArgsConstructor
public class PrivateBankClientImpl implements PrivateBankClient {
    private final WebClient webClient;

    @Override
    public List<PrivateBankExchangeRate> getExchangeRates() {
        return webClient.get()
            .uri(uriBuilder -> uriBuilder.path("/pubinfo")
                .queryParam("exchange")
                .queryParam("coursid", "5")
                .build())
            .retrieve()
            .bodyToMono(new ParameterizedTypeReference<List<PrivateBankExchangeRate>>() {
            })
            .block();
    }
}
