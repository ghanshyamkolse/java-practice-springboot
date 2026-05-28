# Day 5 Q&A: TDD, Agile Methodologies, & Frontend (JavaScript/AngularJS)

### Q1: How do you mock external HTTP calls during integration testing in Spring Boot?
> **Answer:**
> We use **WireMock**.
> 1. WireMock spins up a local in-memory HTTP server on a configured port (e.g. `8089`).
> 2. We construct stubs pointing to expected request URIs and configure the HTTP methods, headers, and response payloads.
> 3. We configure our Spring Boot client properties to point to the WireMock port instead of the real URL.
> 4. We execute our client class. WireMock intercepts the outbound call and returns the stubbed response, enabling us to test deserialization, timeouts, and error scenarios.

---

### Q2: How would you debug a performance issue in an AngularJS application showing slow rendering?
> **Answer:**
> Slow rendering is typically caused by too many `$watchers` in the Digest Cycle.
> 1. Use profiling tools to inspect watcher count (ideally keep watchers under 2000 per page).
> 2. **Optimize loops:** Add `track by` to all `ng-repeat` directives (e.g. `ng-repeat="item in items track by item.id"`). This tells AngularJS to update only mutated rows in the DOM.
> 3. **Remove watchers:** Replace standard double-curly brackets with one-time binding `{{::item.value}}` for static text.
> 4. **Limit filters:** Avoid invoking controller functions directly inside HTML templates, as they run on every single digest cycle.

---

### Q3: What is the difference between `@Mock` and `@MockBean`?
> **Answer:**
> * **`@Mock`** is a standard Mockito annotation. It instantiates a plain mock object. No Spring ApplicationContext is loaded, making tests run extremely fast. It is used in pure unit testing (typically with `@ExtendWith(MockitoExtension.class)`).
> * **`@MockBean`** is a Spring Boot annotation. It creates a Mockito mock and inserts it into the loaded Spring `ApplicationContext`, replacing any existing bean of the same type. It is used in slice tests (like `@WebMvcTest` or `@SpringBootTest`).

---

### Q4: How do you handle estimate conflicts during Sprint Planning?
> **Answer:**
> We use **Planning Poker** to estimate Story Points relatively.
> 1. If developers vote with widely differing estimates (e.g., one votes '2' and another votes '13'), we halt voting.
> 2. We ask the highest and lowest voters to explain their reasoning. The '13' voter might have identified hidden technical debt, while the '2' voter might have solved a similar problem before.
> 3. After discussion, the team revotes until consensus is reached, ensuring shared understanding of task complexity.
