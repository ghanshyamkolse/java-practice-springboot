# Day 2 Q&A: Messaging and Event-Driven Design

### Q1: When would you use a message broker instead of a synchronous REST call?

**Answer:** Use messaging when the producer should not wait for consumers, traffic needs buffering, or several independent consumers need the same event. REST is often simpler when the caller needs an immediate answer, as in the current reading service's meter check. Messaging improves decoupling but introduces eventual consistency and additional failure modes.

### Q2: Kafka or RabbitMQ—which would you choose?

**Answer:** Kafka is a strong fit for retained event streams, replay, and multiple consumer groups reading at their own pace. RabbitMQ is a strong fit for queue-based work distribution and flexible routing. I would choose based on retention, replay, routing, ordering and operational needs, not on a claim that one is always faster or better.

### Q3: What is idempotency and why does a consumer need it?

**Answer:** Idempotency means repeated processing has the same business effect as processing once. A broker may redeliver after a timeout or a crash that occurred after the database commit but before acknowledgement. I would include a stable event ID and persist processed IDs or enforce a unique business constraint.

### Q4: What is a dead-letter queue?

**Answer:** It holds messages that could not be processed under the configured retry policy. It prevents invalid messages from being retried indefinitely and gives operators a way to inspect, alert on, and deliberately repair or replay them. A dead-letter queue should not become an ignored dumping ground.

### Q5: What problem does the transactional outbox solve?

**Answer:** It prevents a crash between committing business data and publishing its event from silently losing one side of the operation. The service writes the business change and outbox row in one local database transaction; a publisher sends the row later. Delivery may repeat, so consumers remain idempotent.

### Q6: Does asynchronous messaging provide an immediate consistent answer to the caller?

**Answer:** No. The producer can confirm that an event was accepted or stored, but consumers may process it later or fail. The API and user experience should represent a pending operation and provide an appropriate way to observe its eventual result.
