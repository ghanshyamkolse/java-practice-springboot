# Day 4 Study Guide: Advanced Java APIs & Spring Framework Internals
*Focus: Java 8-17 features, Spring Beans lifecycle, and design patterns.*

---

## 1. Modern Java Features (Up to Java 17)

### Java 8 Core
* **Streams:** Functional operations on collections (e.g., `.stream().filter(...).map(...).collect(...)`).
* **Lambdas:** Implementation of functional interfaces using lambda expressions `() -> ...`.
* **Optional:** Container object indicating presence or absence of value, designed to prevent `NullPointerException`.
* **CompletableFuture:** Facilitates asynchronous, non-blocking programming patterns by enabling task callback chaining.
* **Date & Time API:** Immutable, thread-safe time representations (`LocalDate`, `LocalDateTime`, `ZonedDateTime`, `Duration`).

### Java 11
* **`var` (Local Variable Syntax):** Reduces verbose type definitions when the compiler can deduce the type.
* **HTTP Client:** Modern built-in HTTP client supporting HTTP/2, reactive programming, and WebSockets.

### Java 17 (LTS)
* **Records:** Immutable data carriers (`public record MyDto(...) {}`). The compiler synthesizes constructor, final fields, accessors, `equals()`, `hashCode()`, and `toString()`.
* **Pattern Matching for `instanceof`:** Conditionally casts object references inline.
* **Sealed Classes:** Dictates inheritance restrictions by explicitly listing permitted subclasses.
* **Text Blocks:** Triple-quote (`"""`) multi-line string structures.

---

## 2. Spring Framework Internals

### A. Spring Bean Lifecycle
1. **Instantiation:** Creates the Java object (constructor execution).
2. **Property Population:** Injects autowired beans and dependencies.
3. **Aware Interfaces:** Inject context details (`BeanNameAware`, `ApplicationContextAware`).
4. **BeanPostProcessor (Before):** Executes custom pre-initialization operations.
5. **Initialization:** Runs `@PostConstruct` or `afterPropertiesSet()`.
6. **BeanPostProcessor (After):** Creates AOP proxies for `@Transactional`, `@Cacheable`, etc.
7. **Destruction:** Executes `@PreDestroy` or `destroy()`.

### B. Core Spring Design Patterns
* **Singleton (Default Scope):** Restricts instantiation to a single bean object per IoC container.
* **Prototype:** Spawns a unique bean object upon every retrieval request.
* **Proxy:** Wraps beans dynamically to run aspects (logging, transactions, security).
* **Factory:** Centralizes IoC container instantiation (`BeanFactory`, `ApplicationContext`).
* **Template:** Eliminates boilerplate code (e.g. `JdbcTemplate`, `RestTemplate`).

---

## 3. Dynamic Proxies & `@Transactional`
* **JDK Dynamic Proxy:** Generated when the target class implements interfaces.
* **CGLIB Proxy:** Generated via subclassing when the target class doesn't implement interfaces (cannot proxy final classes/methods).
* **`@Transactional` Interception:** Dynamic proxy intercepts invocation, starts database transaction via transaction manager, commits on success, and rolls back on RuntimeExceptions.
* **Self-Invocation Issue:** Calling transactional methods internally on `this` bypasses the proxy wrapper, preventing transaction boundary establishment.
