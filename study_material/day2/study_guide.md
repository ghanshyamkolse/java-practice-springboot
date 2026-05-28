# Day 2 Study Guide: Distributed Messaging & Asynchronous Architecture
*Focus: Event-driven systems, scale decoupling, and messaging brokers.*

---

## 1. Message Brokers: Kafka vs. RabbitMQ
Decoupling microservices using asynchronous message passing is critical for system resilience and performance.

### Architectural Comparison
* **RabbitMQ (Smart Broker / Dumb Consumer):** Uses AMQP (Advanced Message Queuing Protocol). The broker actively tracks message acknowledgments, delivery, and queue positions, deleting messages once they are consumed. Highly flexible routing patterns are supported.
* **Apache Kafka (Dumb Broker / Smart Consumer):** An append-only commit log file structure. Kafka writes events sequentially to disk partitions. It does not track consumer state; instead, consumers read sequentially by holding their offset pointer. Replay of historical events is fully supported.

### RabbitMQ Routing Topologies
* **Direct Exchange:** Exact match between routing key and binding key.
* **Fanout Exchange:** Broadcasts messages to all bound queues.
* **Topic Exchange:** Evaluates wildcards (e.g., `*.telemetry.#`) to route messages.

---

## 2. Asynchronous Patterns & Reliability

### A. Acknowledgment Modes
* **Automatic Acknowledgment (Auto-Ack):** The broker deletes the message immediately upon sending it. If the consumer crashes before completion, the message is permanently lost.
* **Manual Acknowledgment (Manual-Ack):** The consumer sends an explicit acknowledgment receipt back to the broker only after the transaction successfully completes. On consumer failure, the broker places the message back in the queue for another consumer.

### B. Dead Letter Queues (DLQ)
Unprocessable messages (poison pills) are isolated into a DLQ after reaching a maximum retry threshold to prevent blocking consumer loops.

### C. Consumer Idempotency
Ensures processing a message multiple times yields the same system state. Implement by checking a database record table for unique message IDs before executing the logic.
