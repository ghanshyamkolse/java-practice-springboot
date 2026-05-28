# Day 6 Study Guide: High Volume Systems, Scalability & Web Servers
*Focus: Caching strategies, Tomcat web server configuration, JVM memory structures, and resolving application bottlenecks.*

---

## 1. Caching Strategies & Topologies

### Local vs. Distributed Caching
* **Local Caching (Caffeine):** Resides in-memory within the JVM heap. Extremely fast (nanoseconds) with no serialization/network overhead. However, data is replicated per node, raising cache drift risk.
* **Distributed Caching (Redis):** Runs as an external cluster. Accessible by all microservice instances, ensuring data consistency and scaling to large datasets. It incurs minor network and serialization overhead.

### Eviction Policies
* **LRU (Least Recently Used):** Evicts items untouched for the longest time.
* **LFU (Least Frequently Used):** Evicts items accessed least often.
* **TTL (Time-To-Live):** Evicts items automatically after a configured duration.

---

## 2. Tomcat & JVM Tuning

### A. Tomcat Thread Pools
* **`maxThreads` (Default: 200):** Sets the maximum concurrent worker threads available to process requests. If downstream calls block, these deplete quickly.
* **`acceptCount` (Default: 100):** Queue capacity for incoming connection requests when all worker threads are busy.
* **`connectionTimeout`:** Maximium wait time for request headers before Tomcat terminates the socket.

### B. JVM Memory Layout
* **Heap Space:** Houses dynamic Java objects. Subdivided into Young Generation (Eden, survivor spaces) and Old Generation. Configured via `-Xms` (min) and `-Xmx` (max).
* **Metaspace:** Holds class metadata. Off-heap storage which grows dynamically (configure `-XX:MaxMetaspaceSize` to prevent system memory leaks).

### C. Garbage Collectors (GC)
* **G1GC (Garbage-First):** Standard GC. Divides the heap into regions, evacuating dead objects incrementally to achieve balanced throughput and latency.
* **ZGC (Z Garbage Collector):** A scalable, ultra-low-latency collector (pauses under 1ms). Compaction steps run concurrently with application threads, consuming slightly more CPU.

---

## 3. High Volume Bottlenecks & Scalability
* **Database Write Locking:** Occurs when transactional writes lock indices. Avoided by using optimistic locking (`@Version`) or batch writing, keeping transactions extremely brief.
* **Load Balancer Algorithms:**
  * *Round Robin:* Routes sequentially to server nodes.
  * *Least Connections:* Routes traffic to the server handling the fewest concurrent requests.
  * *Source IP Hash:* Computes hash on client IP to ensure persistent session routing (session affinity).
