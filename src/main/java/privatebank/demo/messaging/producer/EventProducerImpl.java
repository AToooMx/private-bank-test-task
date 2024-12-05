package privatebank.demo.messaging.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import privatebank.demo.messaging.config.RabbitMqConfigProperties;
import privatebank.demo.model.ExchangeRate;

import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventProducerImpl implements EventPublisher {
    private final RabbitMqConfigProperties properties;
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    public void send(ExchangeRate event) {
        var rabbitMq = properties.getExchangeRates();
        try {
            var message = objectMapper.writeValueAsString(event);
            rabbitTemplate.convertAndSend(rabbitMq.getExchange(),
                rabbitMq.getRoutingKey(),
                MessageBuilder
                    .withBody(message.getBytes(StandardCharsets.UTF_8))
                    .setContentType(MessageProperties.CONTENT_TYPE_JSON)
                    .build());
            log.info("EventProducerImpl.send(): event published, event = {}", message);
        } catch (Exception ex) {
            log.error("EventProducerImpl.send(): event has not been published, event = {}, error = {}",
                event, ex.getMessage(), ex);
        }
    }

}
