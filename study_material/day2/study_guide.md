# Day 2: Messaging and Event-Driven Microservices

**Goal:** Understand when services should communicate with HTTP and when they should publish events.

## 1. Commands, queries, and events

- A **command** asks a named service to do something: `RegisterMeter`.
- A **query** asks for current data: `GET /api/meters/meter-1`.
- An **event** records a fact that has already happened: `ReadingRecorded`.

An event describes something that occurred; it is not a request that guarantees another service will act. Keep event names and payloads stable, meaningful, and versionable.

## 2. Synchronous HTTP versus asynchronous messaging

The current reading service makes a synchronous HTTP request to the meter service. The caller waits for the result, which is straightforward and immediately reports whether the meter exists. It also couples response time and availability: if the meter service is unavailable, the reading request may fail.

With messaging, the producer publishes an event to a broker and consumers process it independently:

```text
Reading Service -> Kafka topic: ReadingRecorded -> Billing Service
                                             \-> Analytics Service
```

This lets consumers run at different speeds and allows new subscribers, but introduces delayed results, message duplication, broker operations, and more complicated debugging. A `202 Accepted` response may be appropriate when work is accepted for later processing; it does not mean downstream work has finished.

## 3. Kafka and RabbitMQ

| Kafka | RabbitMQ |
|---|---|
| Durable, partitioned event log; consumers track offsets | Broker-managed queues with flexible exchanges and routing |
| Consumers can replay retained events by resetting offsets | Messages are generally removed after successful acknowledgement |
| Useful for event streams and several independent consumer groups | Useful for task queues and complex work routing |

Neither broker is universally better. Choose based on retention, replay, routing, ordering, throughput, delivery, and operational requirements. Partition Kafka events by a stable key such as `meterId` when per-meter order is needed.

## 4. Delivery, duplicates, and ordering

In distributed systems, retries and failures can cause a consumer to see a message more than once. Design consumers to be **idempotent**: processing the same event ID again should not apply the business change twice. Enforce deduplication with a persisted event ID or a unique constraint.

Ordering is usually limited to a queue or Kafka partition, not every event in the entire system. A consumer can process messages in an order different from their publication order if its design allows concurrent work.

## 5. Acknowledgements and failed messages

A consumer acknowledges a message after successful processing. If processing fails, the broker can redeliver it according to its configuration. Retry only failures likely to recover (such as brief network unavailability); repeated retries do not fix invalid data.

A dead-letter queue/topic stores messages that exceeded retry policy for inspection and repair. Record the failure reason, alert on a growing dead-letter queue, and define how corrected events are replayed. Avoid silently discarding poison messages.

## 6. The dual-write problem and outbox

Suppose a service commits a reading to its database and then publishes an event. A crash between those actions can leave a saved reading with no event. Publishing first can create the opposite inconsistency.

The **transactional outbox** solves this by storing the business change and an outbox record in the same local database transaction. A separate publisher sends pending outbox records to the broker and marks them delivered. Consumers still need idempotency because delivery can repeat.

## 7. Exercises

1. Draw the current reading-request HTTP path and identify where it waits.
2. Design a `ReadingRecorded` event with an event ID, meter ID, value, and timestamp.
3. Add a billing consumer as a separate application; do not let it read the reading-service database.
4. Simulate redelivery and prove that the consumer does not duplicate a billing entry.
5. Explain how the outbox prevents a database write from being lost before event publication.

## Interview recap

Explain the specific requirement that favors messaging, the consistency delay it introduces, how duplicates are handled, and how failed messages are inspected or replayed.
