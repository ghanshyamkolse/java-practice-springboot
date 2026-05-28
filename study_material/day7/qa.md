# Day 7 Q&A: Scenario Practice & Final Mock Prep

### Q1: Detail how you would design an ingestion system for 10 million smart meters reporting readings every 15 minutes.
> **Answer:**
> 1. **Ingress & Gateway:** I would expose REST/MQTT endpoints protected by **Spring Cloud Gateway**. The gateway acts as a security filter and uses a **Redis RequestRateLimiter** to prevent DDoS or malfunctioning devices from exhausting backend threads.
> 2. **Ingestion Queue:** Gateway forwards events to **Apache Kafka**. I would set up a `telemetry-readings` topic with multiple partitions (e.g., 32 or 64). Events will be partitioned by `meterId` to guarantee that readings from a specific meter are processed sequentially.
> 3. **Processor Microservices:** A Spring Boot consumer microservice will consume Kafka records in batches.
> 4. **Caching & DB Shielding:** For each reading, the service needs to validate configuration rules (e.g., threshold limits). Rather than querying a relational database on every read, active configurations are cached in a **Redis cluster**.
> 5. **Hybrid Storage:** Raw readings write to **MongoDB** due to its scale-out write capabilities. Aggregated hourly summaries and customer billing profiles write to **SQL Server** to guarantee strict transactional compliance (ACID).

---

### Q2: How do you handle microservice authentication in a distributed environment?
> **Answer:**
> We use **OAuth2 combined with JSON Web Tokens (JWT)**.
> 1. Clients request authentication from an Identity Provider (like Keycloak or Okta) and receive a signed JWT access token.
> 3. The client attaches this token to the `Authorization: Bearer <token>` header of incoming API Gateway requests.
> 4. The API Gateway (or downsteam microservices using `spring-boot-starter-oauth2-resource-server`) verifies the token signature locally using public keys exposed by the Identity Provider. This allows authentication verification without making remote database calls.
> 5. Service-to-service communication is secured using **mTLS (Mutual TLS)** or client credentials grant tokens.

---

### Q3: What is the difference between rolling deployment and blue-green deployment?
> **Answer:**
> * **Rolling Deployment:** The deployment system replaces old instances of the application with new ones one-by-one. No additional infrastructure is needed, but at any point, two different versions of the code are active in production, requiring backward-compatible API changes.
> * **Blue-Green Deployment:** Two identical environments exist (Blue is current live; Green is new). We deploy the new code fully to the inactive Green environment. Once verified, the load balancer router shifts 100% of traffic to Green. It allows instant rollback, but doubles infrastructure cost.
