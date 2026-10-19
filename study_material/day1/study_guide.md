# Day 1: Microservices, REST, Gateway, and Service Discovery

**Goal:** Understand what makes an application a microservice system, then trace a request through the runnable energy project.

## 1. What is a microservice?

A microservice is an independently deployable application organized around a business capability. It owns its business rules and data, communicates through explicit APIs or events, and can be changed or scaled without rebuilding every other service.

Splitting code into several folders is not enough. Independent deployment, clear ownership, and the ability to operate services separately matter. Microservices also add network failures, deployment work, and operational overhead, so a modular monolith may be the better starting point for a small system.

## 2. The running project

The project under [`microservices/`](../../microservices/README.md) has four Spring Boot applications and a separately deployed browser UI:

| Application | Purpose |
|---|---|
| Learning UI (`3000`) | Browser forms to register meters, submit readings, view history, and follow the request flow |
| Discovery server (`8761`) | Eureka registry where service instances register |
| API gateway (`8080`) | Public entry point; routes `/api/meters/**` and `/api/readings/**` |
| Meter service (`8081`) | Owns meter records and meter APIs |
| Reading service (`8082`) | Owns readings and checks meter IDs by calling the meter service |

The UI is a static Nginx application, not a Spring Boot business service. It forwards same-origin `/api` requests to the gateway so the browser does not need direct access to internal service addresses.

Each business service has its own H2 database. The reading database stores a meter ID, not a foreign key into the meter service's database. The services can therefore change their storage independently.

Request flow:

```text
Browser -> Learning UI / Nginx -> API Gateway -> Reading Service -> Meter Service
                                                      |                 |
                                                      v                 v
                                                 Reading DB         Meter DB

                                      Gateway and services use Eureka for service discovery.
```

Open <http://localhost:3000>, register a meter, submit a reading, and select a meter to view its reading history. Open the browser Network panel and compare its same-origin `/api` requests with gateway routes. Check Docker logs to follow the gateway and service requests. The gateway is also available directly at `8080`; meter and reading services expose debugging ports `8081` and `8082` in the default Compose setup.

## 3. REST and HTTP basics

REST is an architectural style commonly implemented with HTTP and JSON. Use resource-oriented paths and standard HTTP semantics:

| Method | Example | Meaning |
|---|---|---|
| `GET` | `/api/meters/meter-1` | Read a resource; should not change it |
| `POST` | `/api/meters` | Create a resource |
| `PUT` | `/api/meters/meter-1` | Replace a resource |
| `PATCH` | `/api/meters/meter-1` | Partially update a resource |
| `DELETE` | `/api/meters/meter-1` | Delete a resource |

Useful response codes: `200` successful read, `201` created, `400` invalid request, `404` missing resource, `409` conflicting state, and `5xx` server/dependency failure. A REST API is not automatically secure or reliable; use HTTPS, authentication, validation, timeouts, and clear error contracts.

SOAP is a protocol with XML messages and a formal WSDL contract. It is still useful for systems that require an established SOAP contract or WS-* standards. Neither REST nor SOAP automatically supplies distributed ACID transactions.

## 4. API gateway

The gateway is a client-facing reverse proxy. Here, route rules send meter paths to `lb://meter-service` and reading paths to `lb://reading-service`; `lb://` asks Spring Cloud to resolve service instances and load balance.

Common gateway responsibilities include routing, authentication enforcement, request limits, and selected cross-cutting headers. Keep business rules and domain data in their owning services. A gateway is not automatically a firewall or authentication system; configure those capabilities explicitly.

## 5. Service discovery with Eureka

Instead of hard-coding a service's machine address, an application registers a service name and instance details with Eureka. A client asks for instances by name; Spring Cloud LoadBalancer selects an available instance.

Discovery is useful when container IP addresses and instance counts change. It is not a health guarantee: registration may lag, and a discovered instance may still fail. Use health checks and explicit client timeouts as well.

## 6. Synchronous calls and failure

The reading service calls the meter service before storing a reading. This is simple to understand, but the write depends on the meter service being reachable. A slow or unavailable dependency can slow or fail the reading request. Network calls have latency and can fail even when both applications are otherwise healthy.

Do not add retries blindly: repeating a write can create duplicates. Configure a finite timeout first. Later, study circuit breakers, idempotency, and asynchronous events.

## 7. Exercises

1. Run the stack using the instructions in [`microservices/README.md`](../../microservices/README.md) and inspect registered apps at <http://localhost:8761>.
2. Create a meter and a reading in the UI. Look at the browser Network panel and the respective container logs.
3. Send a reading for a nonexistent meter. Explain the `404` and identify which service owns the validation.
4. Stop the meter service and submit another reading. Observe what this simple synchronous design does.
5. Add an endpoint to update a meter's status without moving meter data into another service.

## Interview recap

Be prepared to explain a service boundary, database ownership, gateway routing, discovery, and one failure caused by a synchronous dependency. For each design choice, explain a reasonable alternative and its trade-off.
