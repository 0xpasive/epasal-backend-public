package epasal.com.orderservice.eventPublisher;

import epasal.com.orderservice.config.RabbitConfig;
import epasal.com.orderservice.event.OrderEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RabbitEventPublisher {
    private final RabbitTemplate rabbitTemplate;

    public void publishOrderPlacedEvent(OrderEvent orderEvent) {
        rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE, RabbitConfig.ORDER_EVENT_ROUTING_KEY, orderEvent);
    }

}
