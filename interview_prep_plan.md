# Java and Microservices Interview Plan

Use this seven-session plan alongside the runnable [energy microservices project](./microservices/README.md). Each session should end with working code and a short explanation you can give in an interview.

| Session | Focus | Hands-on result |
|---|---|---|
| 1. Service basics | REST, UI-to-gateway requests, service boundaries, Eureka, synchronous calls | Register a meter in the UI, submit a reading, and trace the request through the services |
| 2. Messaging | Kafka vs. RabbitMQ, events, retries, dead-letter queues, idempotency | Add a `ReadingRecorded` event and a consumer |
| 3. Data | Database-per-service, SQL/JPA, transactions, indexes, N+1 queries | Persist readings independently and explain why services do not share tables |
| 4. Java and Spring | Java 17, dependency injection, bean lifecycle, transactions, configuration | Explain the code path for recording one reading |
| 5. Testing | Unit, web-slice and integration tests; Mockito; test-driven development | Test validation, service failures and message handling |
| 6. Resilience and scale | Timeouts, circuit breakers, caching, load balancing, metrics and tracing | Stop a dependency and observe controlled failure and recovery |
| 7. System design and interview | Telemetry architecture, trade-offs, STAR behavioral answers | Present the system, its bottlenecks and the next scaling steps |

## A repeatable study routine

For each session:

1. Read the matching guide and questions under [`study_material/`](./study_material/).
2. Run the system and make one small change related to that topic.
3. Test the success path and at least one failure case.
4. Explain the design choice, an alternative, and its trade-off aloud.
5. Write down one interview answer using a real example and a measurable result.

## System design summary to practice

Smart meters submit readings through an API gateway. The gateway routes to independent services discovered through Eureka. The reading service validates a meter by calling the meter service and stores readings in its own database. As the system grows, publish reading events to Kafka so billing can process them asynchronously; add idempotency and an outbox to handle duplicate delivery and database/broker consistency.

At each stage, be ready to explain the trade-off: synchronous HTTP is easy to follow but couples availability; messaging improves decoupling but introduces eventual consistency and operational complexity. Start with the current project, then add resilience, persistence, security and observability as separate exercises.

## Interview answer checklist

- State the requirement and constraints before proposing components.
- Explain service ownership, request/event flow and data consistency.
- Discuss failure cases: timeouts, retries, duplicate events and unavailable dependencies.
- Mention measurable service-level goals such as latency, throughput and availability.
- Compare alternatives and state why the chosen design fits the constraints.
- Use STAR (Situation, Task, Action, Result) for behavioral questions; never invent metrics.
