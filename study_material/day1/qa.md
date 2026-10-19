# Day 1 Q&A: Microservices and HTTP APIs

### Q1: What makes an application a microservice?

**Answer:** It is independently deployable, owns a focused business capability and its data, and communicates through a defined API or events. Splitting one application into packages alone does not create microservices. This independence improves deployment and scaling options but adds networking and operations complexity.

### Q2: Walk me through a request to create a reading in the practice project.

**Answer:** The client sends `POST /api/readings` to the gateway on port `8080`. The gateway resolves `reading-service` using Eureka and routes the request. The reading service validates the payload, calls `meter-service` to check that the meter exists, then saves the reading in its own database. It does not query the meter service's database.

### Q3: Why does each service own its own database?

**Answer:** Ownership lets each service change its schema and persistence independently and protects its business rules. Other services use its API or events instead of shared tables. This avoids tight schema coupling, but cross-service reporting and transactions require deliberate designs.

### Q4: What are the responsibilities of an API gateway and service discovery?

**Answer:** The gateway is a client-facing entry point that routes requests and can enforce explicitly configured cross-cutting policies. Eureka records service instances under logical names; clients use those names and a load balancer instead of hard-coded instance addresses. Neither replaces health checks, timeouts, or security configuration.

### Q5: What is the main weakness of the reading service's current HTTP call?

**Answer:** Recording a reading depends synchronously on the meter service. A network delay or meter-service outage can delay or fail the reading request. A short timeout limits waiting; a circuit breaker can reduce repeated calls to a failing dependency. An event-driven design is another option, but introduces eventual consistency and duplicate-message handling.

### Q6: How do REST and SOAP differ?

**Answer:** REST is an architectural style commonly built on HTTP resources and JSON. SOAP is a protocol based on XML messages and formal service contracts such as WSDL. Both can be secure when configured correctly; neither inherently gives a distributed application an ACID transaction across services.
