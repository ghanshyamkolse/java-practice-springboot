package day2.messaging;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

/**
 * Message listener for processing smart meter readings from RabbitMQ.
 * Relies on RabbitMQ retry rules. If the max-attempts threshold is reached,
 * the exception is not caught and the message is routed to the configured DLQ.
 */
@Service
public class MeterReadingConsumer {

    /**
     * Listens to the main queue. In case of custom validation failures (e.g. poison pills),
     * it throws an exception to trigger Spring AMQP's listener retry container.
     */
    @RabbitListener(queues = RabbitMqConfig.MAIN_QUEUE)
    public void receiveMeterReading(String rawReadingPayload) {
        System.out.println("Consuming message from RabbitMQ: " + rawReadingPayload);

        // Simulate poison pill scenario
        if (rawReadingPayload != null && rawReadingPayload.contains("corrupted")) {
            throw new IllegalArgumentException("Payload validation failed. Poison pill detected!");
        }

        // Processing logic...
        processTelemetry(rawReadingPayload);
    }

    private void processTelemetry(String data) {
        // Business logic here
        System.out.println("Processing telemetry record: " + data);
    }
}
