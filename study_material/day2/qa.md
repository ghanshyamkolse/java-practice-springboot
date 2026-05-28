# Day 2 Q&A: Distributed Messaging & Asynchronous Architecture

### Q1: How does messaging help in high-volume applications?
> **Answer:**
> Messaging decouples services and enables **asynchronous processing** (load leveling). 
> Instead of a user request executing synchronous, blocking downstream calls, the request is published to a broker and a success code is returned immediately. 
> A pool of downstream worker services consumes the queue at a sustainable rate, protecting systems during sudden traffic spikes.

---

### Q2: What is a "poison pill" message and how do you handle it in Spring Boot?
> **Answer:**
> A poison pill is a message that cannot be processed due to corruption, invalid schemas, or business errors. 
> In Spring AMQP (RabbitMQ) or Spring Kafka:
> 1. If unhandled, it will fail and retry indefinitely, blocking the queue or partition.
> 2. We handle this by configuring a **Dead Letter Queue (DLQ)**. 
> 3. After a maximum number of retries (e.g., 3), the message is routed to the DLQ exchange/topic, freeing up the consumer for the next message.

---

### Q3: When should you choose Kafka over RabbitMQ?
> **Answer:**
> * Choose **Kafka** if you need:
>   * Event streaming, real-time log analysis, or event sourcing.
>   * Message replayability (ability to rewind offsets and re-process).
>   * High horizontal scalability (millions of events per second) using partitions.
> * Choose **RabbitMQ** if you need:
>   * Complex routing logic (e.g., routing based on wildcard keys or headers).
>   * Strict message-level delivery tracking and transition state handled by the broker.
>   * Easy integration of legacy protocols.

---

### Q4: How do you achieve exactly-once processing in Kafka?
> **Answer:**
> Exactly-once processing requires two layers:
> 1. **Kafka Transactions:** Producers write messages across multiple partitions transactionally using `KafkaTemplate.executeInTransaction()`.
> 2. **Consumer Idempotency:** The consumer must keep track of processed message IDs (e.g., using a database de-duplication table with a unique constraint) to ensure duplicate deliveries are discarded.
