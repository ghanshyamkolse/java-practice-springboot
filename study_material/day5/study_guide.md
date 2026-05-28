# Day 5 Study Guide: TDD, Agile Methodologies, & Frontend (JavaScript/AngularJS)
*Focus: JUnit/Mockito testing patterns, Scrum mechanics, and legacy AngularJS optimizations.*

---

## 1. Test-Driven Development (TDD)
A development technique where tests are written before implementation code. It ensures test coverage and modular class designs.

### Core Testing Layers in Spring
* **`@SpringBootTest`:** Loads the full ApplicationContext. Slower to run; best suited for full system integration verifications.
* **`@WebMvcTest`:** Boots only the web slice (Spring MVC, controllers, routing, JSON serializations). Service layers must be mocked via `@MockBean`.
* **`@DataJpaTest`:** Boots the persistence slice (in-memory H2 database, repositories). Integrates transaction management, rolling back data modifications after every test.
* **Mockito:** A mocking framework. Use `@Mock` for mock dependencies, `@InjectMocks` on the target class, and `@Spy` for partial mocks.
* **WireMock:** Spins up a mock HTTP server to return simulated responses, enabling complete testing of outgoing REST/SOAP templates without using live remote servers.

---

## 2. Agile (Scrum) Methodologies
A framework enabling teams to deliver valuable increments iteratively.

### Core Roles
1. **Product Owner:** Manages the product backlog and dictates business value priorities.
2. **Scrum Master:** Eliminates team roadblocks and guides Scrum process execution.
3. **Developers:** Deliver working sprint deliverables.

### Agile Ceremonies
* **Sprint Planning:** Commits backlog deliverables into the Sprint scope.
* **Daily Standup:** Alignment check-in covering work done, daily focus, and blockages.
* **Refinement (Grooming):** Cleans up product backlogs, refines tasks, and assigns relative size estimates (Story Points) using Planning Poker.
* **Sprint Review:** Demos completed increments to stakeholders.
* **Sprint Retrospective:** Focuses on internal process improvements.

---

## 3. Frontend: AngularJS (1.x Legacy Framework)
Understanding the connection between legacy JS clients and Spring Boot REST interfaces.

### Core Mechanics
* **Two-Way Binding:** Model adjustments sync to the DOM, and form inputs update the JS model in real-time.
* **Digest Cycle & `$watchers`:** AngularJS registers watchers for bound variables. When an event fires, it evaluates watcher states against previous values (dirty checking) recursively until values stabilize.

### Performance Bottlenecks & Fixes
1. **One-Time Data Binding (`::`):** Binds values that never mutate (e.g. `{{::meter.name}}`), removing watchers after initial rendering.
2. **`track by`:** Dictates distinct key tracking inside `ng-repeat` loops (e.g., `track by meter.id`), letting the browser reuse DOM elements instead of rebuilding them.
3. **Digest Minimization:** Avoid placing complex logic inside view interpolation loops.
