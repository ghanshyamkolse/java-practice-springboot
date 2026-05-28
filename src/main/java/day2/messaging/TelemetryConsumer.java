package day2.messaging;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

/**
 * Message listener for processing telemetry data from Apache Kafka.
 * Connects to the 'telemetry.readings' topic using the 'energy-group' consumer group.
 */
@Service
public class TelemetryConsumer {

    /**
     * Consumes records from Kafka. If an exception occurs, the error handler configured
     * in {@link KafkaConfig} catches it, retries, and then publishes to the DLT.
     */
    @KafkaListener(topics = "telemetry.readings", groupId = "energy-group")
    public void consumeTelemetry(String partitionPayload) {
        System.out.println("Received record from Kafka: " + partitionPayload);

        // Simulate logic validation failure
        if (partitionPayload != null && partitionPayload.startsWith("invalid")) {
            throw new RuntimeException("Payload parsing failed. Sending to Kafka DLT!");
        }

        // Process data...
        saveToLogs(partitionPayload);
    }

    private void saveToLogs(String data) {
        System.out.println("Persisted telemetry log to storage: " + data);
    }
}
