package day2.messaging;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

/**
 * Apache Kafka Configuration establishing retry bounds and Dead Letter Topic (DLT) recoverer.
 */
@Configuration
public class KafkaConfig {

    /**
     * Configures a listener container error handler.
     * Uses DeadLetterPublishingRecoverer to forward failed messages to 'originalTopicName.DLT'
     * after executing 3 retry attempts with a 1-second fixed interval backoff.
     */
    @Bean
    public DefaultErrorHandler defaultErrorHandler(KafkaTemplate<Object, Object> kafkaTemplate) {
        // Publishes failed records to dynamic Dead Letter Topic
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(kafkaTemplate);
        
        // 3 total attempts: 1 initial delivery + 2 retries, 1000ms delay between retries
        return new DefaultErrorHandler(recoverer, new FixedBackOff(1000L, 2L));
    }
}
