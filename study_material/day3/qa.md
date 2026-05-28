# Day 3 Q&A: Databases, JDBC, & Data Access

### Q1: How do you identify and resolve slow-running queries in a SQL Server + Spring Boot application?
> **Answer:**
> 1. **Identify:** Enable SQL logging in development configurations (`logging.level.org.hibernate.SQL=DEBUG` and parameter binding logs). In production, use database profiling tools like **SQL Server Profiler** or view execution statistics dynamically.
> 2. **Analyze:** Run the slow SQL query directly in SQL Server Management Studio and review the **Execution Plan**. Inspect if it is performing table/index scans instead of index seeks.
> 3. **Resolve:** 
>    * Overwrite poorly generated Hibernate SQL with JPQL queries that include `JOIN FETCH` to resolve N+1 query problems.
>    * Introduce non-clustered covering indexes for high-frequency queries.
>    * Tune HikariCP connection pool configurations (e.g. `maximumPoolSize`) to avoid connection starvation issues.

---

### Q2: When would you store data in MongoDB instead of SQL Server in a Digital Energy solution?
> **Answer:**
> * Choose **MongoDB** for high-frequency smart meter readings, log archives, or IoT telemetry streams. These streams generate high-volume, semi-structured records where schema requirements change often and write speeds are critical.
> * Choose **SQL Server** for core business transactions that require absolute relational consistency, foreign keys, and ACID compliance (e.g., customer profiles, account billing data, tariff rates).

---

### Q3: What is the difference between `@EntityGraph` and `JOIN FETCH`?
> **Answer:**
> * **`JOIN FETCH`** is explicitly defined inside a JPQL query (e.g. `SELECT m FROM SmartMeter m JOIN FETCH m.readings`). It forces an SQL `INNER` or `LEFT JOIN` to fetch the child records.
> * **`@EntityGraph`** is an annotation config that can be applied to repository methods (e.g. `@EntityGraph(attributePaths = {"readings"})`). It instructs Spring Data to alter the fetch strategy dynamically. If you use a basic repository method like `findById()`, Spring builds the join fetch logic dynamically behind the scenes.

---

### Q4: How do you prevent thread starvation in HikariCP?
> **Answer:**
> Starvation occurs when application threads wait longer than `connectionTimeout` to get a JDBC connection.
> 1. Keep database transactions as short as possible. Avoid executing external network/API requests inside active `@Transactional` methods.
> 2. Right-size the pool: Configure `maximumPoolSize` to match system load and database server capacities.
> 3. Set a reasonable `connectionTimeout` (e.g. 30000ms) and configure `leakDetectionThreshold` (e.g., 2000ms) to identify threads that hold connections longer than expected.
