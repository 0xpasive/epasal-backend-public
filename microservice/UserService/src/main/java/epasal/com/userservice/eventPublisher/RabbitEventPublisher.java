package epasal.com.userservice.eventPublisher;

import epasal.com.userservice.config.RabbitConfig;
import epasal.com.userservice.event.UserCodeEvent;
import epasal.com.userservice.event.UserRegisteredEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class RabbitEventPublisher {
    private final RabbitTemplate rabbitTemplate;

    public void publishUserRegistrationEvent(UserRegisteredEvent event) {
        log.debug("Publishing user registration event for user: {}", event.getEmail());
        try {
            log.debug("Sending user registration event to RabbitMQ for user: {}", event.getEmail());
            rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE, RabbitConfig.USER_EVENT_ROUTING_KEY, event);
        } catch (AmqpException e) {
            log.error("Failed to send user registration event for user: {}", event.getEmail(), e);
        }
    }

    public void publishUserCodeEvent(UserCodeEvent event) {
        try {
            rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE, RabbitConfig.USER_CODE_ROUTING_KEY, event);
        } catch (AmqpException e) {
            log.error("Failed to send verification code event to RabbitMQ: {}", e.getMessage());
            log.info("The code is: {}", event.getCode());
        }
    }
}
