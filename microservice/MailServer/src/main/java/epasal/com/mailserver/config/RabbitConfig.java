package epasal.com.mailserver.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class RabbitConfig {
    public static final String EXCHANGE = "app.exchange";

    public static final String ORDER_EVENT_QUEUE = "order.queue";
    public static final String ORDER_EVENT_ROUTING_KEY = "user.order";

    public static final String USER_EVENT_QUEUE = "user.registered.queue";
    public static final String USER_EVENT_ROUTING_KEY = "user.registered";

    public static final String USER_CODE_QUEUE = "user.code.queue";
    public static final String USER_CODE_ROUTING_KEY = "user.code";

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(EXCHANGE);
    }

    @Bean
    public Queue userRegisteredQueue() {
        return new Queue(USER_EVENT_QUEUE);
    }

    @Bean
    public Queue userCodeQueue() {
        return new Queue(USER_CODE_QUEUE);
    }

    @Bean
    public Queue orderQueue() {
        return new Queue(ORDER_EVENT_QUEUE);
    }

    @Bean
    public Binding userRegisteredBinding() {
        return BindingBuilder
                .bind(userRegisteredQueue())
                .to(exchange())
                .with(USER_EVENT_ROUTING_KEY);
    }

    @Bean
    public Binding userCodeBinding() {
        return BindingBuilder
                .bind(userCodeQueue())
                .to(exchange())
                .with(USER_CODE_ROUTING_KEY);
    }

    @Bean
    public Binding orderBinding() {
        return BindingBuilder
                .bind(orderQueue())
                .to(exchange())
                .with(ORDER_EVENT_ROUTING_KEY);
    }

    @Bean
    public MessageConverter messageConverter() {
        return new JacksonJsonMessageConverter();
    }

}
