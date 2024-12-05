package privatebank.demo.messaging.producer;

import privatebank.demo.model.ExchangeRate;

public interface EventPublisher {

    void send(ExchangeRate exchangeRate);

}
