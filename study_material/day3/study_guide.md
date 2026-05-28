# Day 3 Study Guide: Databases, JDBC, & Data Access
*Focus: SQL Server, MongoDB, Connection Pooling, and Spring Data JPA tuning.*

---

## 1. Relational vs. NoSQL

### SQL Server (Relational)
* Relational engine with strong ACID guarantees.
* Default isolation level: **Read Committed** (prevents dirty reads but allows non-repeatable reads).
* **Indexes:** 
  * *Clustered Index:* Physically orders the rows on disk (only one allowed per table, typically the primary key).
  * *Non-Clustered Index:* Logical index pointing to the clustered index key.

### MongoDB (NoSQL Document Store)
* Stores data in flexible, schema-less binary JSON (BSON) format.
* **Scaling:** Highly scalable horizontally using **Sharding** (distributing database chunks across shards based on a shard key) and **Replica Sets** (for replication and high availability).

---

## 2. Spring Data JPA & Hibernate Tuning

### A. The N+1 Query Problem
Occurs when loading a collection association (e.g. `SmartMeter` has `@OneToMany` of `MeterReading`). The application runs 1 query to fetch $N$ smart meters, then performs $N$ extra queries to fetch readings for each meter.

#### How to Solve
1. **JPQL `JOIN FETCH`:** Performs an inner or left join in SQL, loading the target and its associations in 1 database round-trip.
2. **`@EntityGraph`:** Annotates a repository method, instructing Hibernate to load the mapped fields eagerly inside a single query.
3. **`@BatchSize`:** Configures Hibernate to load associated entities in batches of size $S$ using `WHERE parent_id IN (?, ?, ...)`, changing total query count to $1 + N/S$.

### B. HikariCP Connection Pooling
A high-performance JDBC connection pool. Essential properties include:
* `maximumPoolSize`: Maximum simultaneous database connections allowed.
* `minimumIdle`: Idle connections maintained inside the pool.
* `connectionTimeout`: Max wait time for a connection before an exception occurs.
* `leakDetectionThreshold`: Logs connection leaks if a query holds a connection too long.

### C. Hibernate Cache Levels
* **First-Level Cache:** Session-scoped cache (enabled by default). It ensures querying the same record inside a single database transaction/session doesn't trigger secondary SQL calls.
* **Second-Level Cache:** Application-wide cache. It caches lookup tables or read-heavy entities across database sessions (requires configuration of providers like Ehcache or Caffeine).

---

## 3. Slow Query Diagnostics
* **Explain Plans / Execution Plans:** Inspect query execution in SQL Server Management Studio to find table scans (absence of matching indexes) and index seeks (efficient tree lookups).
* **Covering Indexes:** Adding query outputs to the `INCLUDE` clause of a non-clustered index, allowing SQL Server to return results directly from the index structure without looking up the base table page.
