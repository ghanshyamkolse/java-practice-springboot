# Day 3: Data Ownership, Databases, and Spring Data JPA

**Goal:** Understand database-per-service and practise the persistence patterns used by the meter and reading applications.

## 1. Database ownership

In this practice system, `meter-service` owns meter registration and `reading-service` owns readings. Each service uses its own H2 database. A reading stores a `meterId` as a value, but has no database foreign key to the meter-service database.

The service that owns a record is responsible for its schema, validation, and migrations. Other services use that service's API or published events. Sharing a database or joining tables across service boundaries couples deployments and undermines the boundary.

An in-memory H2 database is useful for learning and tests; it loses data when the application restarts. A production service would choose and operate durable storage based on its consistency, scale, query, and recovery requirements.

## 2. Relational and document databases

Relational databases such as PostgreSQL or SQL Server fit structured data, constraints, joins, and transactions. Document databases such as MongoDB fit document-shaped data and can support flexible structures and horizontal distribution. Either may be appropriate depending on access patterns and operational needs; high write volume alone does not automatically mean MongoDB is the right choice.

Consider the queries, consistency rules, data shape, retention, backup, and scale before choosing storage. Avoid storing the same authoritative record in multiple services without a synchronization and ownership strategy.

## 3. Entity and repository basics

The meter service maps `Meter` to its own `meters` table. The reading service maps `Reading` to its own `readings` table and indexes `meter_id` because that column is used to retrieve a meter's readings. Spring Data repositories provide common CRUD operations and queries derived from method names.

The reading API validates its request, checks the meter through `MeterClient`, and writes a row through `ReadingRepository`. The remote call is deliberately outside the database write transaction: do not keep a database connection and lock open while waiting on another service.

## 4. Transactions and cross-service consistency

A database transaction protects work inside one service's local database. A normal Spring `@Transactional` transaction does not atomically include an HTTP call, another service's database, or Kafka. If multiple services must coordinate a business process, consider a saga with local transactions and compensating actions, or an asynchronous event flow.

Keep transactions short. Avoid remote calls inside a transaction: network delays extend lock and connection occupancy, and a remote service cannot be rolled back by the local database.

## 5. Indexes and query performance

An index can speed up reads that filter or sort on indexed columns, but uses storage and adds work to inserts and updates. Build indexes around actual query patterns and confirm their value with the database execution plan and measurements.

The reading repository filters by `meterId` and orders by `recordedAt`; its meter-ID index is a starting point, not a substitute for measuring and designing a composite index if the data volume and query plan justify one.

## 6. N+1 queries and fetch strategy

An N+1 problem occurs when an application loads a collection of parent entities, then runs a separate query for each parent's children. Inspect generated SQL before fixing it. `JOIN FETCH`, entity graphs, or batch fetching can help, but each has trade-offs, especially for collection pagination and duplicate result rows.

The current meter and reading models intentionally do not have a JPA relationship across service boundaries. Do not solve a cross-service data need by adding a JPA association to another service's table.

## 7. Connection pools

HikariCP maintains reusable JDBC connections. Pool size is limited by database capacity as well as the application. A large pool can overload the database rather than make queries faster.

Investigate pool wait time, connection usage, query duration, and transaction duration before tuning. `connectionTimeout` bounds how long a caller waits for a connection; leak detection is a diagnostic aid, not a substitute for closing connections and keeping transaction scopes correct.

## 8. Exercises

1. Restart `reading-service` and confirm that its in-memory readings disappear; explain why this is not durable storage.
2. Trace `POST /api/readings` and identify which service writes each row.
3. Add a query for readings for one meter and inspect the generated SQL and index usage.
4. Explain why creating a foreign key to the meter service's database would introduce coupling.
5. Propose a persistent store and migration strategy without changing the service ownership boundary.

## Interview recap

Describe data ownership, local transactions, indexing trade-offs, and how you would implement a business process that spans multiple services without assuming a distributed ACID transaction.
