package day2.messaging;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ Configuration defining main queue, dead-letter queue (DLQ),
 * exchanges, and their respective routing bindings.
 */
@Configuration
public class RabbitMqConfig {

    public static final String MAIN_QUEUE = "meter.readings.queue";
    public static final String DLQ = "meter.readings.dlq";
    public static final String EXCHANGE = "energy.exchange";
    public static final String DLQ_EXCHANGE = "energy.dlq.exchange";
    public static final String ROUTING_KEY = "meter.readings";

    // 1. Declare the Dead Letter Queue
    @Bean
    public Queue deadLetterQueue() {
        return QueueBuilder.durable(DLQ).build();
    }

    // 2. Declare the Main Queue configured to route failed messages to the DLQ exchange
    @Bean
    public Queue mainQueue() {
        return QueueBuilder.durable(MAIN_QUEUE)
                .withArgument("x-dead-letter-exchange", DLQ_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", "dead-letter")
                .build();
    }

    // 3. Declare Direct Exchanges
    @Bean
    public DirectExchange mainExchange() {
        return new DirectExchange(EXCHANGE);
    }

    @Bean
    public DirectExchange dlqExchange() {
        return new DirectExchange(DLQ_EXCHANGE);
    }

    // 4. Bind the Main Queue to the Main Exchange
    @Bean
    public Binding mainBinding() {
        return BindingBuilder.bind(mainQueue())
                .to(mainExchange())
                .with(ROUTING_KEY);
    }

    // 5. Bind the Dead Letter Queue to the DLQ Exchange
    @Bean
    public Binding dlqBinding() {
        return BindingBuilder.bind(deadLetterQueue())
                .to(dlqExchange())
                .with("dead-letter");
    }
}
