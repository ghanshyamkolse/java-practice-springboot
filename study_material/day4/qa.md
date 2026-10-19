# Day 4 Q&A: Java and Spring Boot

### Q1: What happens when a request reaches `POST /api/readings`?

**Answer:** Spring maps the URL to the controller, converts JSON to a request record, runs validation, calls the meter-service client, saves a reading through the local repository, and serializes a response DTO. The meter lookup is a network call; the reading insert is a local database operation.

### Q2: Why use constructor injection?

**Answer:** Required dependencies are explicit, immutable after construction, and straightforward to supply in tests. Spring creates the controller and supplies the repository and client beans.

### Q3: Why use DTOs instead of returning a JPA entity?

**Answer:** A DTO defines the API contract independently of database mappings. It avoids unintentionally exposing internal columns, lazy-loading relationships, or persistence details, and allows the API and schema to evolve separately.

### Q4: What do Java records provide, and when would you use them?

**Answer:** Records provide concise data-carrier classes with final components, an accessor for each component, and generated equality and representation methods. They fit simple request or response DTOs. They are not a direct replacement for mutable JPA entities or richer domain objects.

### Q5: What is the Spring self-invocation issue with `@Transactional`?

**Answer:** Spring often applies transactions through a proxy. A call from one method to another on the same object can bypass that proxy, so the inner method's transactional advice may not run. Place transaction boundaries on externally invoked service methods or use an architecture that calls through the managed proxy.

### Q6: How does container configuration differ from local configuration?

**Answer:** Local defaults are declared in YAML, while Compose injects environment-specific values such as port and Eureka URL. Container DNS names locate other containers; `localhost` means the current container. Secrets should be provided securely rather than committed in YAML.
