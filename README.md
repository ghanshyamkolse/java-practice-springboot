# Java and Microservices Interview Practice

This repository is a hands-on Java/Spring Boot learning project. It contains the original interview exercises and a separate, runnable microservices example built around a smart-energy system.

## Microservices learning project

The independent applications live in [`microservices/`](./microservices/):

| Application | Port | Responsibility |
|---|---:|---|
| Discovery server (Eureka) | 8761 | Keeps track of service instances |
| API gateway | 8080 | Routes external API requests to services |
| Meter service | 8081 | Registers and looks up smart meters; owns meter data |
| Reading service | 8082 | Stores readings; verifies meter IDs over HTTP |
| Learning UI (Nginx) | 3000 | Interactive meter and reading forms; proxies requests to the gateway |

Each business service owns its own H2 database. The reading service does not read the meter service's tables; it discovers and calls the meter service through Eureka. The gateway uses service discovery and load balancing to route requests. Open the learning UI at <http://localhost:3000> to register meters, submit readings, view reading history, and follow an illustrated explanation of the request flow. The UI is a separately deployed Nginx service; it is not a Spring Boot domain service.

### Run the services

Requirements: Java 17+ and Docker with Docker Compose.

From the repository root:

```powershell
docker --context default compose -f microservices\compose.yml up --build
```

Open the learning UI at <http://localhost:3000>. You can also inspect Eureka at <http://localhost:8761> or send requests directly through the gateway at <http://localhost:8080>:

```powershell
Invoke-RestMethod -Method Post -Uri http://localhost:8080/api/meters `
  -ContentType 'application/json' -Body '{"meterId":"meter-1","status":"ACTIVE"}'

Invoke-RestMethod -Method Post -Uri http://localhost:8080/api/readings `
  -ContentType 'application/json' -Body '{"meterId":"meter-1","value":12.5}'

Invoke-RestMethod -Uri http://localhost:8080/api/readings/meter/meter-1
```

Stop the services with `docker --context default compose -f microservices\compose.yml down`. The `default` Docker context is specified because Docker Desktop's selected `desktop-linux` context may not be running. The databases are in-memory, so data is lost when a service restarts.

### Build and test

```powershell
.\mvnw.cmd -f microservices\pom.xml clean verify
```

The original single-application interview exercises remain at the repository root and can be tested separately:

```powershell
.\mvnw.cmd test
```

## Suggested study order

1. Start every service and inspect registration in Eureka.
2. Send meter and reading requests through the gateway; compare the gateway URL with direct service URLs.
3. Stop the meter service and observe how the reading service's synchronous call fails.
4. Scale a service to multiple instances and observe discovery/load balancing.
5. Add timeouts, retries and a circuit breaker; test failure and recovery.
6. Add Kafka and an outbox so readings are processed asynchronously and reliably.
7. Add billing, service-owned persistent databases, authentication, tracing, metrics and deployment automation.

For the interview-preparation schedule, see [`interview_prep_plan.md`](./interview_prep_plan.md). For the service architecture and next exercises, see [`microservices/README.md`](./microservices/README.md).
