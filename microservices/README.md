# Energy microservices learning lab

This is a runnable learning system with an interactive UI and four independently deployed Spring Boot applications. Use the UI to try the APIs, then follow requests through the gateway, Eureka, business services, and their separately owned databases.

## Request flow

```text
Browser (:3000)
  | same-origin /api requests
  v
Learning UI / Nginx (separate container)
  | HTTP /api/meters/** or /api/readings/**
  v
API Gateway (:8080) -- discovers destinations through Eureka
  |                                           |
  v                                           v
Meter Service (:8081) <--- HTTP ------ Reading Service (:8082)
  |                                           |
  v                                           v
Meter H2 database                     Reading H2 database

Discovery Server / Eureka (:8761) <--- service registrations
```

The browser is served by the separate `learning-ui` Nginx container on port `3000`. Nginx also proxies `/api` requests to the gateway, so the browser uses a same-origin URL. The gateway is the API entry point. Eureka provides service names and instance locations. The reading service checks that a meter exists by calling the meter service; it stores only the meter ID with its reading. Each business service has its own application, build module, and database. Do not add a foreign key from one service's database to another.

## Use the learning UI

Open <http://localhost:3000>. The page has three practical operations:

1. **Register a meter.** The browser sends `POST /api/meters` to the UI container. Nginx forwards it to the gateway, which discovers the meter service. The meter service validates and saves the meter to its database.
2. **Record a reading.** The reading service first calls the meter service to verify the meter, then saves the reading to the reading database. A missing meter returns a clear error and is not saved.
3. **View history.** The browser asks for the selected meter's readings. The gateway routes to the reading service, which reads its own database.

The UI also explains the service map, request lifecycle, and trade-offs on the page. Its JavaScript is plain browser JavaScript, with no front-end package installation or build step. Nginx serves the UI and proxies API calls; it is a separate container and deployment, not a Spring Boot business-domain microservice.

### Live service status and request paths

The service strip shows the UI as online when the page is loaded and checks the gateway, Eureka, meter service, and reading service using their Actuator health endpoints. Checks run when the page opens and every 15 seconds; select **Check status** to run them immediately. `UP` means that service's health endpoint returned `UP`; `DOWN` means the endpoint reported a non-UP health status, while `UNREACHABLE` means the request timed out or the proxy could not connect. This is a basic health signal, not proof that every dependency or business operation is ready. For example, Eureka can be healthy while a business service is not registered yet.

Nginx exposes only four exact `/ops/health/...` proxy paths for this display; it does not proxy arbitrary Actuator endpoints to the browser. These checks report service health, not end-to-end API success. Use the forms to test actual operations.

In **Follow the request**, choose **Register meter**, **Record reading**, or **View history** to preview the relevant service path. When you submit a form, change the selected meter, or refresh history, the path animates while that API request is in flight and then shows its HTTP result and elapsed time. The path is an illustration of the expected route, not distributed tracing; use the browser's Network panel and container logs for individual service activity.

## API examples

All paths work through the gateway on port `8080` and through the UI proxy at port `3000`. For direct debugging, meter and reading services are also exposed on ports `8081` and `8082`.

| Method | Path | Purpose |
|---|---|---|
| `POST` | `/api/meters` | Register a meter: `{"meterId":"meter-1","status":"ACTIVE"}` |
| `GET` | `/api/meters` | List registered meters |
| `GET` | `/api/meters/{meterId}` | Look up a meter |
| `POST` | `/api/readings` | Record a reading: `{"meterId":"meter-1","value":12.5}` |
| `GET` | `/api/readings/meter/{meterId}` | List readings for a meter |

An unknown meter returns `404` when a reading is submitted. Invalid payloads return `400`; duplicate meter IDs return `409`.

## Run and test

From the repository root:

```powershell
docker --context default compose -f microservices\compose.yml up --build
```

The command above streams logs from all containers. To follow only the Spring Boot applications in a separate terminal (without the UI's Nginx logs), run:

```powershell
docker --context default compose -f microservices\compose.yml logs -f discovery-server api-gateway meter-service reading-service
```

Meter registration/lookups and reading operations are logged by the business services at `INFO`; expected conflicts and missing-meter cases are logged at `WARN`. Request payload values are not logged.

```powershell
.\mvnw.cmd -f microservices\pom.xml clean verify
```

For local development without Docker, start each application in a separate terminal from the repository root:

```powershell
.\mvnw.cmd -f microservices\pom.xml -pl discovery-server spring-boot:run
.\mvnw.cmd -f microservices\pom.xml -pl meter-service spring-boot:run
.\mvnw.cmd -f microservices\pom.xml -pl reading-service spring-boot:run
.\mvnw.cmd -f microservices\pom.xml -pl api-gateway spring-boot:run
```

The local applications default to Eureka at `http://localhost:8761/eureka/`. Each H2 database is in memory and is intentionally isolated; restarting its service clears its data. Use `docker --context default compose -f microservices\compose.yml down` to stop the Docker deployment.

## What to learn next

The UI and first service flow are working foundations. The remaining items are guided implementation milestones; they are **not** already implemented. Complete them in order, keep the app running, and use each acceptance check to explain what changed.

### 1. Service boundaries — map ownership before adding features

**Question:** Which service owns a rule and its authoritative data?

1. Keep meter registration, status, and meter-ID uniqueness in `meter-service`.
2. Keep reading validation, recorded values, timestamps, and history in `reading-service`.
3. Add a written operation table before adding APIs: operation, owner, data written, dependent service, failure response.
4. Add one feature (for example meter deactivation) only to its owning service. Define what should happen if a client attempts a reading from a deactivated meter.

**Done when:** each field has one authoritative owner; no service queries another service's database; a service's API contract communicates business decisions.

### 2. Discovery and routing — run more than one instance

Keep API clients on the gateway and service-to-service calls on Eureka service names. To observe the current instances, open <http://localhost:8761> or query Eureka's applications endpoint.

```powershell
docker --context default compose -f microservices\compose.yml -f microservices\compose.scale.yml up -d --scale meter-service=2
docker --context default compose -f microservices\compose.yml -f microservices\compose.scale.yml ps
```

The scale override removes meter-service's fixed host port mapping, which would otherwise conflict when Compose starts a second instance. Container-to-container calls through Eureka and the gateway continue to work; the gateway and Eureka remain available on the host. Run `docker --context default compose -f microservices\compose.yml -f microservices\compose.scale.yml ps` to inspect the replica containers.

**Important experiment:** each replica currently has a separate in-memory H2 database. Create a meter, make repeated lookups, and observe why an unshared database makes stateless load balancing incorrect for this use case. Do not treat a successful scale-up as proof that instance-local data is safe.

**Done when:** explain service-name lookup, instance selection, and why shared durable service-owned storage is needed before this stateful service can be scaled safely.

### 3. Failure handling — timeouts before retries

1. Configure connection and response/read timeouts on the reading service's meter HTTP client.
2. Stop the dependency with `docker --context default compose -f microservices\compose.yml stop meter-service`.
3. Submit a reading through <http://localhost:3000> and verify the request fails within the defined time budget, does not create a reading, and displays an error rather than a false success. Observe the meter-service health indicator as well.
4. Restart it with `docker --context default compose -f microservices\compose.yml start meter-service`.
5. Add a circuit breaker with observable `CLOSED`, `OPEN`, and `HALF-OPEN` transitions. Do not add a fallback that claims the reading was stored.
6. Retry only transient failures and only when duplicate business effects are prevented; never blindly retry writes.

**Done when:** the outage is bounded, visible, and recoverable, and its HTTP response accurately describes what happened.

### 4. Asynchronous events — add billing without a synchronous request chain

1. Add Kafka to Compose and create a small, separate billing-service application.
2. Publish a versioned `ReadingRecorded` event with a stable event ID, meter ID, value, and timestamp.
3. Partition by meter ID only if per-meter ordering is a business requirement.
4. Make the billing consumer idempotent: redelivering the same event ID must not create a duplicate charge.
5. Add a transactional outbox in reading-service so committing a reading and scheduling its event cannot silently diverge.
6. Add bounded retry and a dead-letter topic for invalid or repeatedly failing events.

**Done when:** billing can process a reading independently; you can explain eventual consistency, replay, duplicate delivery, and how operators inspect failed events.

### 5. Data ownership — replace volatile storage

1. Add a persistent database container for meter-service and a different database/container for reading-service.
2. Give each service its own schema, credentials, migration files, and data volume. Never point both services at a shared set of tables.
3. Replace `ddl-auto: update` with versioned migrations and validate schema changes on service startup.
4. Restart a service and prove its own records persist. Stop one database and verify the other service's data remains isolated.
5. Decide and document how you will back up, restore, and evolve each database.

**Done when:** data survives restarts and no service can read or write another service's database with its credentials.

### 6. Observability and security — make requests traceable and access intentional

1. Add a correlation/trace ID at the gateway and propagate it to downstream requests and logs.
2. Emit structured logs and service metrics: request rate, error rate, latency, dependency failures, database-pool usage, and (after Kafka) consumer lag.
3. Add dashboards and alerts for a sustained error increase or stalled queue; never log credentials or tokens.
4. Configure identity-provider-backed authentication and authorization for clients. Restrict service-to-service traffic with explicit service identity rather than trusting the internal network alone.
5. Add TLS where the deployment boundary requires it and keep signing keys/passwords out of source and container images.

**Done when:** follow one request across gateway and both services using one trace ID; show which callers may perform each operation; verify no secret appears in logs.

### 7. Deployment — test startup, readiness, and reproducible changes

1. Add Docker health checks to the Spring services (for example, actuator health) as well as the UI.
2. Configure Compose to wait for dependencies to become healthy, not merely start their processes.
3. Keep local defaults separate from deployment configuration and provide credentials through a local `.env` or secret mechanism; do not commit secret values.
4. Build each service image from its own source/module and pin base image versions. Check that a clean checkout can build and start the full stack.
5. Add a repeatable end-to-end smoke test: wait for readiness, register a meter, create a reading, fetch it, verify an unknown-meter error, then clean up.
6. Add CI to run unit, integration, and Compose smoke tests before publishing images.

**Done when:** a clean environment can build, start, health-check, smoke-test, and shut down the system using documented commands, with no manual code edits.

## Stop, start, and observe the Docker stack

Run from the repository root:

```powershell
docker --context default compose -f microservices\compose.yml up --build -d
docker --context default compose -f microservices\compose.yml ps
docker --context default compose -f microservices\compose.yml logs -f learning-ui api-gateway reading-service meter-service
docker --context default compose -f microservices\compose.yml down
```

Include `-f microservices\compose.yml` when running the Compose commands in the learning milestones from the repository root. For example, stop and restart the meter service with:

```powershell
docker --context default compose -f microservices\compose.yml stop meter-service
docker --context default compose -f microservices\compose.yml start meter-service
```

Published URLs: UI <http://localhost:3000>, gateway <http://localhost:8080>, Eureka <http://localhost:8761>. For direct debugging, the meter and reading services are published at `8081` and `8082` in the default Compose setup. When using the scaling override, the meter service is reached through the gateway because its host port is deliberately unpublished. Database contents currently disappear when the corresponding service restarts.
