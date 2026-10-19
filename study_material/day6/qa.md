# Day 6 Q&A: Resilience and Scaling

### Q1: How would you diagnose a sudden increase in `5xx` responses?

**Answer:** Check request rate, latency, errors, CPU/memory, thread state, connection-pool wait, database latency, and downstream calls. Correlate gateway and service logs using a trace/request ID. Identify the saturated resource before increasing a thread or pool limit, which could merely overload a dependency further.

### Q2: What is the difference between a timeout and a circuit breaker?

**Answer:** A timeout bounds how long one call may wait. A circuit breaker tracks recent outcomes and temporarily stops calls when a dependency is failing, allowing it to recover and avoiding repeated load. A breaker does not replace a timeout, health checks, or clear error handling.

### Q3: When is retrying a request unsafe?

**Answer:** Retrying a non-idempotent write can apply the operation more than once, and retrying a persistent failure can amplify load. Retry only transient failures with a finite limit and backoff; use idempotency keys or another duplicate-protection strategy for writes.

### Q4: What is the difference between a local cache and a distributed cache?

**Answer:** A local cache resides in one application instance, so it is fast but may be inconsistent with caches on other replicas. A distributed cache is shared but adds a network dependency. Choose based on freshness, latency, capacity, availability, and invalidation requirements.

### Q5: Does adding more service instances always increase capacity?

**Answer:** No. Replicas help when the service's own compute is the limit and requests can be distributed. A shared database, broker, network, or external service may be the real bottleneck. Replicas also must not rely on isolated local state when shared state is required.

### Q6: What is a bulkhead?

**Answer:** It limits how much of a service's threads or other resources one dependency or workload may consume. If that workload slows down, other operations can still make progress. The limits must be chosen and monitored with workload measurements.

### Q7: What do logs, metrics, and traces tell you?

**Answer:** Logs explain events, metrics reveal aggregate trends and saturation, and traces show the path and timing of an individual request across services. Together they make it easier to distinguish a gateway issue from a slow database or downstream service.
