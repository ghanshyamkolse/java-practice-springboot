# Day 4 Q&A: Advanced Java APIs & Spring Framework Internals

### Q1: How does Spring implement `@Transactional` under the hood?
> **Answer:**
> Spring uses **AOP dynamic proxies** (JDK Dynamic Proxies or CGLIB).
> 1. When a bean containing `@Transactional` is injected, Spring instantiates a proxy wrapper.
> 2. When a thread calls a transactional method, the proxy intercepts the execution, accesses the `PlatformTransactionManager`, and opens a database connection/transaction.
> 3. The proxy calls the target bean's method.
> 4. If successful, the proxy commits the transaction. If a `RuntimeException` (or configured exception) is thrown, it triggers a database rollback.

---

### Q2: What is the "self-invocation" transaction issue and how do you resolve it?
> **Answer:**
> * **The Issue:** Since `@Transactional` is managed by a proxy wrapper, calling a transactional method from another method *inside the same class* bypasses the proxy. The call is invoked directly on `this`, meaning no transaction is created.
> * **Resolutions:**
>   1. **Refactoring (Recommended):** Move the transactional method to a separate Spring component (e.g. `BillingHelper`).
>   2. **Self-Injection:** Inject the bean into itself using `@Autowired` combined with `@Lazy` to force execution through the proxy.
>   3. **Programmatic Transactions:** Use `TransactionTemplate` manually to wrap the execution.

---

### Q3: What is the difference between BeanFactory and ApplicationContext?
> **Answer:**
> * **`BeanFactory`:** The root interface of Spring's IoC container. It provides basic dependency injection support, lazy-loads beans by default (instantiates beans only when requested), and has lightweight memory utilization.
> * **`ApplicationContext`:** Extends `BeanFactory`. It provides advanced features including eager bean pre-instantiation at startup, internationalization (i18n) support, event publishing mechanisms, and automatic registration of `BeanPostProcessor` beans.

---

### Q4: What are Java 17 Records and when should you use them?
> **Answer:**
> Records are special classes designed to act as immutable data carriers. They reduce boilerplate significantly.
> * Use them for **DTOs (Data Transfer Objects)**, request/response models, database mappings (where entities don't require mutation), or keys inside hash maps.
> * Avoid them for JPA entities because Hibernate depends on lazy loading, proxies, and mutable structures which clash with a Record's final fields and lack of default constructors.
