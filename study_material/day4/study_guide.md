# Day 4: Java 17 and Spring Boot in the Microservices Project

**Goal:** Explain how a Spring application starts, wires dependencies, validates an API request, and performs a local database operation.

## 1. Java 17 features used in service code

- **Records** are concise data carriers. `MeterRequest` and `ReadingRequest` use records for immutable request DTOs.
- **Streams** transform collections, for example mapping repository entities to response DTOs.
- **`Optional`** represents a possibly absent result, as in `findByMeterId`.
- **`Instant`** represents a point on the UTC timeline and is used for reading timestamps.
- **Text blocks** make JSON test payloads readable.

Use a feature when it clarifies the code. A record is useful for a simple DTO; it is not automatically an entity or a substitute for domain validation.

## 2. Application startup and dependency injection

Each service has its own `@SpringBootApplication` class and process. Spring Boot configures components from its classpath and application configuration, discovers classes in the package, and starts the relevant web server.

Constructor injection makes dependencies visible and required. For example, `ReadingController` receives a `ReadingRepository` and a `MeterClient`; Spring supplies their registered beans. A `@Bean` method in `ReadingServiceApplication` creates a load-balanced `RestClient.Builder`.

`@SpringBootApplication` combines configuration, auto-configuration, and component scanning. Keep application classes and their components in a clear package hierarchy so component scanning does not depend on a list of unrelated packages.

## 3. One request through the reading service

For `POST /api/readings`:

1. Spring MVC matches the route to `ReadingController`.
2. Jackson converts JSON to `ReadingRequest`.
3. Bean Validation checks constraints such as a non-blank meter ID and a non-negative value.
4. `MeterClient` asks the meter service to verify the ID using its discovered service name.
5. `ReadingRepository` persists a `Reading` in the reading service's own database.
6. The controller returns a response DTO.

Separating request/response DTOs from entities keeps the external JSON contract independent of the persistence mapping.

## 4. Configuration by environment

`application.yml` sets local defaults. Environment variables such as `SERVER_PORT` and `EUREKA_SERVER_URL` override values in the container. Configuration belongs outside the executable artifact when it varies between environments. Do not put passwords, tokens, or private keys in source control.

Compose DNS names such as `discovery-server` work between containers. `localhost` inside a container refers to that same container, not a different service.

## 5. Spring transactions and proxies

Spring commonly implements `@Transactional` through a proxy around a bean. Calls passing through the proxy establish the configured transaction boundary. Self-invocation—one method calling another method on `this`—may bypass proxy advice. Transactions generally roll back on unchecked exceptions by default; check the configured transaction manager and rollback rules for exact behavior.

Do not add a transaction annotation just because a method writes data. Identify the local atomic operation first, keep it short, and do not include a remote HTTP call in a database transaction without a deliberate reason.

## 6. Exercises

1. Trace the `ReadingRequest` data from JSON through validation to the persisted `Reading`.
2. Explain why `MeterClient` is an interface and how `DiscoveryMeterClient` implements it.
3. Change an environment variable and observe how the container configuration changes.
4. Add a field to a response DTO without returning a JPA entity directly.
5. Identify which part of the flow is local database work and which part crosses the network.

## Interview recap

Explain dependency injection, request validation, DTOs versus entities, configuration, and the limits of proxy-based transactions using an example from a running service.
