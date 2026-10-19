# Day 7 Q&A: System Design and Interview Practice

### Q1: How would you design ingestion for 10 million devices reporting every 15 minutes?

**Answer:** First, I would clarify burst patterns, payload size, latency and availability targets, retention, ordering, duplicate behavior, and billing requirements. The evenly spread average is about 11,100 readings per second, but I would size for measured or agreed peak traffic. I would validate authenticated device requests at the edge and use a durable, partitioned stream when buffering and independent consumers are required. I would partition by meter ID only if per-meter ordering is needed and verify capacity with load tests.

### Q2: How do you ensure a reading is not billed twice?

**Answer:** Give the reading a stable event ID and make billing idempotent, enforcing deduplication in its own database with a unique constraint or equivalent. Design retries and replay as expected behavior. Keep enough audit information to explain which source readings produced each billing result.

### Q3: How do you handle a reading that arrives after billing has already run?

**Answer:** Define an explicit late-data policy with the business: reject it, include it in a later adjustment, or recalculate a billing period. Model corrections as auditable operations or events. Do not assume that transport-level ordering or exactly-once broker features alone solve business consistency.

### Q4: How would you secure the services?

**Answer:** Authenticate clients with an identity provider and verify access tokens; authorize each operation based on identity and permissions. Use HTTPS externally and suitable service identities or mTLS/tokens internally. Keep credentials out of source and logs, and do not rely on network location or the gateway as the only authorization boundary.

### Q5: How would you handle a downstream outage?

**Answer:** Bound calls with a timeout. Use limited retries with backoff only for transient failures and idempotent operations. A circuit breaker can fail fast after repeated failures, and a queue can buffer work if the business flow can be asynchronous. Return an honest error or pending status rather than success-shaped fallback data.

### Q6: How do you decide whether to split a service?

**Answer:** Look for a clear business capability, data ownership, change boundary, scaling need, or team ownership reason. Compare the benefit of independent deployment with the cost of network calls, observability, operations, and distributed consistency. If independence is not needed yet, a well-structured modular monolith may be simpler.

### Q7: How would you validate a proposed high-volume architecture?

**Answer:** Define a representative workload and service-level goals, then load-test ingestion and downstream consumers. Measure latency percentiles, error rates, consumer lag, resource saturation, database throughput, and recovery after failure. Increase traffic in steps and document observed bottlenecks instead of assuming a technology's advertised capacity applies.

### Q8: Which parts of this interview project are production-ready?

**Answer:** The project is a learning baseline. It demonstrates separate Spring Boot services, Eureka discovery, gateway routing, local H2 databases, and a synchronous HTTP dependency. It still needs durable database storage, production security, explicit timeouts and resilience policies, observability, automated integration testing, and operational deployment design before it could be considered production-ready.
