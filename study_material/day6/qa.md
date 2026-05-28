# Day 6 Q&A: High Volume Systems, Scalability & Web Servers

### Q1: If your Spring Boot service starts throwing 503 Service Unavailable errors under load, how do you diagnose it?
> **Answer:**
> 1. **Check Infrastructure Metrics:** Inspect CPU, RAM, and disk I/O. If CPU/RAM is at 100%, we are resource-constrained and need scaling.
> 2. **Analyze Thread Dumps:** Generate thread dumps (`jcmd` or `jstack`). If all Tomcat executor threads (`http-nio-...-exec`) are in `BLOCKED` or `TIMED_WAITING` states, look at their stack traces. They are likely blocked waiting on database connections or slow downstream REST endpoints.
> 3. **Review Connection Pools:** Starvation in HikariCP will block threads. We verify slow queries in database execution logs.
> 4. **Remediate:** Optimize slow SQL queries, introduce caching, scale Tomcat threads (`server.tomcat.threads.max`), implement circuit breakers to fail-fast on downstream timeouts, or scale out horizontally.

---

### Q2: What is the difference between Cache-Aside and Write-Through caching?
> **Answer:**
> * **Cache-Aside (Lazy Loading):** The application handles both data stores. It checks the cache. On a miss, it queries the database, writes the result to the cache, and returns. Cache only populates on-demand, saving memory, but initial requests suffer latency.
> * **Write-Through:** The application writes directly to the cache, which synchronously updates the database. The cache is always consistent with the database, but write latency increases due to two synchronous writes.

---

### Q3: When would you use G1GC over ZGC?
> **Answer:**
> * Choose **G1GC** if you have standard heap sizes (e.g. under 4GB-8GB), can tolerate GC pause times of 100-200ms, and want to maximize overall throughput with lower CPU overhead.
> * Choose **ZGC** if you are running large heaps (up to terabytes) and have strict real-time latency SLAs requiring GC pauses to stay under 1-2 milliseconds, and you have sufficient spare CPU cycles to run concurrent GC compaction threads.

---

### Q4: How does optimistic locking differ from pessimistic locking in JPA?
> **Answer:**
> * **Optimistic Locking:** Uses a version column (annotated with `@Version`). Hibernate checks the version during updates. If another transaction changed the version, an `OptimisticLockException` is thrown. No actual database row locks are held, maximizing read/write performance.
> * **Pessimistic Locking:** Instructs the database to lock the row directly (e.g., SQL Server `SELECT ... WITH (UPDLOCK)`). Other transactions are blocked from reading or writing until this transaction completes. It guarantees consistency but severely limits throughput under high volume.
