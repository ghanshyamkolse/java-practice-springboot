# Day 7: System Design and Mock Interview

**Goal:** Explain a smart-meter system clearly, state its assumptions, and defend the trade-offs.

## 1. Start with requirements

Before drawing components, ask:

- How many devices and readings arrive, and are reports evenly distributed or bursty?
- What is the maximum acceptable ingestion and query latency?
- Can readings arrive late, out of order, or more than once?
- How long must readings be retained, and what must be available during an outage?
- Does billing require an exact audit trail and reproducible calculations?

For example, 10 million devices reporting once every 15 minutes produce about 11,100 readings per second on average. Real traffic may peak much higher if devices report together. Throughput, payload size, retention, query patterns, and availability targets must be clarified before selecting infrastructure.

## 2. Draw the current learning system

```text
Device / client
      |
      v
API Gateway -> Eureka-based routing
      |                  |
      v                  v
Meter Service      Reading Service --HTTP--> Meter Service
      |                  |
Meter database      Reading database
```

This system demonstrates the gateway, discovery, independently running services, separate databases, API validation, and a synchronous service-to-service call. It is a learning baseline, not a solution that is already proven to process 10 million devices.

## 3. Evolve the design against the requirements

### Ingestion

Authenticate devices, validate payloads, enforce per-device limits, and accept readings through a stable API. If the ingestion response should not wait for storage, write the accepted event to a durable broker and return a response that accurately means “accepted,” not “billed.”

### Buffering and processing

Use a partitioned event stream such as Kafka when durable retention, replay, and independent consumers are needed. Partition by meter ID when per-meter ordering is a requirement. Scale consumer groups based on observed lag and partition count.

Validate schemas and timestamps, handle duplicates with event IDs, and define how poison events reach an observable dead-letter flow. Add a transactional outbox if a database write and event publication must not diverge.

### Data and billing

Keep service ownership boundaries. Store raw readings according to their retention, write, and query patterns. Billing calculations should be reproducible and auditable; design idempotent processing and correction events for late or amended readings. Use a saga when a business workflow spans services and needs compensating actions—not as a claim that every action can be erased.

### Availability and security

Use finite timeouts, carefully bounded retries, health/readiness checks, circuit breakers where useful, and capacity limits. Authenticate callers and authorize actions explicitly; use HTTPS and secure service-to-service identity. A gateway alone does not make downstream services trusted.

### Observability and operations

Measure throughput, end-to-end latency, error rate, consumer lag, database capacity, and availability. Propagate trace/correlation IDs, protect sensitive data in logs, and rehearse dependency outages and recovery.

## 4. Key trade-offs to explain

| Choice | Benefit | Cost or risk |
|---|---|---|
| Synchronous HTTP | Immediate response; simple request flow | Couples latency and availability |
| Asynchronous events | Buffers work; decouples consumers | Delayed results, duplicates, broker operations |
| Local cache | Very low-latency reads | Per-instance staleness and invalidation |
| Shared cache | Shared entries across instances | Network dependency and cache availability |
| More replicas | More application compute capacity | Does not fix a saturated shared database |
| Service split | Independent ownership and deployment | Network and operational complexity |

## 5. Behavioral answers with STAR

- **Situation:** Set the real project context; do not invent scale or outcomes.
- **Task:** State the responsibility or measurable goal.
- **Action:** Describe what you personally investigated, designed, changed, and tested.
- **Result:** State a measured result when you have one. If you do not, explain what evidence you would collect instead of inventing a metric.

## 6. Mock interview exercise

Give a five-minute design explanation:

1. State requirements and assumptions.
2. Sketch the components and the request/event path.
3. Assign a clear owner to each data set.
4. Walk through success, duplicate delivery, and a service outage.
5. Explain a consistency and storage choice.
6. Name the metrics and next experiment you would use to validate the design.

Finish by comparing your proposal to the running project: identify which components exist today and which are proposed future work.
