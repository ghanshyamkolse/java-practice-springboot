# Day 6: Resilience, Scaling, Caching, and Observability

**Goal:** Identify bottlenecks and prevent one unhealthy dependency from causing an uncontrolled failure cascade.

## 1. Start with a measurable target

Before tuning, define the workload and service-level objectives: request rate, latency percentiles, error rate, and availability. A useful first question is whether the bottleneck is the application, database, dependency, network, or deployment capacity. Measure before increasing thread or connection counts.

## 2. Scale out and balance requests

Horizontal scaling runs multiple instances of a stateless service. Eureka advertises instances and Spring Cloud LoadBalancer chooses a service instance for `lb://service-name` calls. Each replica should be independently ready and able to serve requests.

Stateful local memory and local caches are not automatically shared between replicas. Multiple instances need durable/shared state or an explicit strategy for partitioning it. Do not assume adding replicas helps a system bottlenecked by one shared database.

Experiment: run a second meter-service instance on a different port with the same service name, then observe discovery and routing. For this learning setup, both instances have separate in-memory H2 databases, so their records are not shared; that illustrates why in-memory per-instance state is not production data storage.

## 3. Timeouts, retries, circuit breakers, and bulkheads

- A **timeout** bounds how long a request waits on a dependency. Set it deliberately and consider an overall request deadline.
- A **retry** can recover from a transient failure, but increases load and can repeat a non-idempotent write. Use a finite limit, backoff, and safe/idempotent operations.
- A **circuit breaker** temporarily rejects calls after a failure threshold, then allows limited probes to see if the dependency recovered. It does not make a dependency healthy or guarantee a valid fallback.
- A **bulkhead** limits resources one operation can consume so that one slow dependency does not exhaust every request thread or connection.

Use these together thoughtfully. A retry policy under a long timeout can make total latency much worse. A fallback must be truthful; do not return fabricated success for work that did not happen.

## 4. Caching

Caffeine is a local in-process cache; Redis is a shared remote cache. Local caching is fast but each replica can have different cached values. A distributed cache can be shared but adds network latency and another dependency.

Cache-aside reads the cache first, loads on a miss, and caches the result. Define expiry, invalidation, size limits, and behavior when the cache is unavailable. A stale cached meter status can affect business decisions, so decide whether the use case can tolerate staleness.

## 5. Back pressure and brokers

A broker can buffer work when consumers cannot keep up, but the queue is finite in practice: disk, retention, and processing delay still matter. Monitor lag and queue depth, and choose an overload policy. Back pressure, rate limiting, and controlled rejection are safer than allowing unlimited work to accumulate.

## 6. Observability

- **Logs** explain individual events; include a request/trace ID and useful structured fields.
- **Metrics** show aggregate health and saturation: request rate, error rate, latency, queue lag, CPU, memory, and database-pool usage.
- **Traces** show where one request spent time across service boundaries.

Use correlation IDs consistently across gateway and downstream calls. Avoid logging tokens, secrets, or sensitive payloads.

## 7. JVM and web-server tuning

Garbage collection, heap sizing, server threads, and JDBC pool settings can influence throughput and latency. Their safe values depend on workload, memory limits, runtime, and database capacity; there is no universal setting that fixes slow requests. Capture metrics and thread dumps, reproduce the bottleneck, change one relevant limit, and compare results.

## 8. Exercises

1. Record request latency and status codes across the gateway and both services.
2. Stop the meter service and compare failures with the service running.
3. Add a finite HTTP timeout to the meter client and test the failure path.
4. Research circuit-breaker states, then add a bounded circuit-breaker exercise with a truthful error/fallback response.
5. Run multiple instances and explain which state is shared and which state is not.

## Interview recap

Describe a failure mode, evidence used to find its cause, the smallest appropriate resilience measure, and the operational metric that proves it helped.
