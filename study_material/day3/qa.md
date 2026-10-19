# Day 3 Q&A: Databases and Data Ownership

### Q1: Why should each microservice own its database?

**Answer:** The service controls its schema, validation, and write rules, so other services cannot silently couple themselves to its tables. Other services use an API or event contract. This allows independent changes but makes cross-service reporting and workflows more explicit.

### Q2: Does `@Transactional` make a call to another microservice atomic?

**Answer:** No. It normally covers one local transaction manager and database. An HTTP request or Kafka publish cannot be rolled back as part of that local database transaction. For a multi-service process, use local transactions and a suitable saga/outbox design, with compensation where required.

### Q3: Why should an HTTP call not be made while holding a database transaction open?

**Answer:** Network calls have unpredictable latency and can fail. Holding the transaction open consumes a connection and may retain locks while waiting, reducing throughput and making recovery harder. Keep the network step outside a short local transaction where possible.

### Q4: How would you investigate a slow repository query?

**Answer:** Reproduce it with representative data, inspect generated SQL and database execution plans, and measure latency and rows read. Check filter/sort indexes and look for N+1 queries. Add or change an index only when measurements show it helps; indexes also add write and storage costs.

### Q5: What is the N+1 query problem?

**Answer:** The application retrieves a list in one query and then issues one additional query per row to load related data. Depending on access patterns, a fetch join, entity graph, batch loading, or a purpose-built projection can reduce round trips. Verify the SQL and account for pagination and collection semantics.

### Q6: Why does the reading service store a meter ID without a foreign key?

**Answer:** The meter record belongs to a different service and database. The reading service validates the ID through the meter service API, then stores the identifier as part of its own record. This avoids a cross-service database dependency; it also means the two services do not share a distributed transaction.

### Q7: Does increasing the database connection pool always improve throughput?

**Answer:** No. Too many concurrent database connections can saturate the database and increase contention. Measure connection wait time, query time, transaction duration, and database capacity before changing pool size.
