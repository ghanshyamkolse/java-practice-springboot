# Day 1 Study Guide: Microservices, RESTful & SOAP Web Services
*Focus: API design, architectural patterns, and legacy vs. modern integration.*

---

## 1. REST vs. SOAP
* **REST (Representational State Transfer):** An architectural style utilizing HTTP protocols. Key features include statelessness, cacheability, standard HTTP methods (GET, POST, PUT, DELETE), and support for multiple media formats (JSON, XML, text).
* **SOAP (Simple Object Access Protocol):** A strict, contract-based protocol. It is XML-only, relies on Web Services Description Language (WSDL) contracts, and has built-in specifications for message-level security (WS-Security) and transactional integrity (WS-AtomicTransaction).

### Key Architectural Differences
* **Message Layer vs Transport Layer Security:** SOAP's WS-Security signs and encrypts the XML payload itself, permitting secure transit over multiple intermediary proxies. REST depends on Transport Layer Security (TLS/HTTPS) to secure the pipe.
* **Transaction Management:** SOAP supports distributed transaction standards natively. REST operations are stateless, requiring logical compensation patterns (like Sagas) to ensure consistency.

---

## 2. Microservices Design Patterns

### A. API Gateway (Spring Cloud Gateway)
Functions as the primary entry point for microservice clients. It handles cross-cutting concerns like authentication, routing, header modification, and rate limiting.
* **Rate Limiting:** Commonly implemented via Redis using the token bucket algorithm. It assigns a key (like Client IP or User ID) and limits how many requests can consume tokens per second.

### B. Service Discovery (Eureka)
Allows microservices to discover one another dynamically.
* **Registration:** Client services register their host, port, and health check URLs with Eureka Server.
* **Resolution:** Client services retrieve the registry map to load balance requests locally using Spring Cloud LoadBalancer.

### C. Resilience Patterns (Resilience4j)
Protects microservices from cascading failures:
1. **Circuit Breaker:** Transitions between `CLOSED` (traffic flows), `OPEN` (fails immediately to protect resources), and `HALF-OPEN` (tests downstream system recovery) based on failure rate thresholds.
2. **Bulkhead:** Limits concurrent executions allowed on a specific service path, isolating failures.
3. **Retry:** Re-executes failed requests with configurable backoff policies.
4. **Rate Limiter:** Limits executions in a specific time-window to prevent resource starvation.

### D. Distributed Tracing (Micrometer + Zipkin)
* **Trace ID:** A unique string propagated across service boundaries to group all logs associated with a single request chain.
* **Span ID:** Identifies individual operations within a single microservice's boundaries.

---

## 3. Distributed Transactions (Saga Pattern)
Sagas coordinate local database transactions across multiple microservices to maintain eventual consistency.
* **Choreography:** Decentralized event-driven approach. Services consume events and run local transactions or emit new events.
* **Orchestration:** Centralized orchestrator class manages order execution and compensation flow.

---

## 4. Consuming SOAP in Spring Boot
Uses `WebServiceTemplate` along with classes compiled from the client's `.wsdl` using JAXB plugins.
* **Marshaller / Unmarshaller:** Translates Java objects into SOAP XML messages (`marshalSendAndReceive`) and vice-versa.
