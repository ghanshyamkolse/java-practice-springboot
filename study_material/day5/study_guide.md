# Day 5: Testing Microservices and Working in Agile

**Goal:** Choose the right test boundary and verify both successful requests and dependency failures.

## 1. Testing layers

| Test type | What it verifies | Typical tools |
|---|---|---|
| Unit test | One class's logic without starting Spring | JUnit, Mockito |
| Web-slice test | Controller routing, JSON, validation, and HTTP responses | `@WebMvcTest`, MockMvc |
| Data-slice test | Repository mappings and queries | `@DataJpaTest` |
| Integration test | Several application components working together | `@SpringBootTest` |
| End-to-end test | A user-visible flow through running services and infrastructure | Docker Compose, HTTP client |

Use the smallest test that proves the behavior. A full application context is slower and should be reserved for integration behavior that a focused test cannot verify.

The meter and reading services include web-slice tests. They verify creation, validation, and relevant conflict/not-found responses while mocking collaborators. The repository does not yet have a full HTTP integration test that starts the complete Compose system; the deployed smoke-test flow provides an initial manual end-to-end check.

## 2. Test behavior and failure paths

Useful API cases include:

- valid request returns the intended status and response fields;
- missing or invalid fields return `400`;
- duplicate meter registration returns `409`;
- reading for an unknown meter returns `404`;
- a downstream timeout or outage produces an explicit failure rather than a fabricated success.

Test public behavior rather than private implementation details. When mocking an HTTP dependency, also test the real HTTP client's serialization and error handling in a separate integration test, for example with WireMock or a test server.

## 3. Test doubles

- A **stub** returns a controlled value.
- A **mock** lets a test verify an interaction.
- A **fake** is a lightweight working implementation, such as an in-memory repository.

Use mocks at boundaries such as a remote service client. Too many mocks can make a test verify an implementation shape rather than useful behavior. A Spring web-slice mock is placed in the test application context; a plain Mockito mock does not require Spring.

## 4. Test-driven development

Use the red-green-refactor loop:

1. Write a failing test for one behavior.
2. Add the smallest production change that makes it pass.
3. Refactor while keeping the test green.

Include edge cases and failure behavior as part of the expected design, rather than adding tests only after implementation is complete.

## 5. Testing distributed behavior

Independent service tests do not prove that gateway routing, discovery, ports, Docker DNS, and HTTP integration work together. A useful end-to-end smoke test:

1. starts Compose and waits for service readiness;
2. creates a meter through the gateway;
3. submits a reading for that meter;
4. retrieves the reading;
5. verifies an unknown meter returns `404`;
6. tears the test environment down reliably.

Avoid brittle tests that depend on a fixed startup sleep. Use health or readiness checks and clean test data or isolated environments.

## 6. Agile discussion

Scrum teams plan a small, valuable increment, inspect progress regularly, review working outcomes, and adapt their process. Refinement clarifies backlog items and acceptance criteria. Estimation is a conversation about relative effort and uncertainty, not a promise of exact hours.

For this project, a focused story could be: “As a client, I can submit a reading for an existing meter and retrieve it later.” Acceptance criteria should include validation, an unknown-meter case, and a documented response shape.

## 7. Optional AngularJS context

AngularJS 1.x is a legacy front-end framework that may appear in older applications. Its digest cycle checks bound values; excessive watchers, expensive template expressions, and large repeated lists can slow rendering. `track by` helps AngularJS identify list rows, and one-time binding (`::`) is useful for values that do not change. Keep this as supporting interview knowledge unless the role specifically requires AngularJS.

## 8. Exercises

1. Run the meter and reading module tests and explain what each mock isolates.
2. Add a test for malformed JSON or a blank meter ID.
3. Add a test ensuring an unknown meter is not persisted as a reading.
4. Write a small integration test for `DiscoveryMeterClient` using a controlled HTTP server.
5. Outline an automated Compose smoke test that waits for readiness rather than sleeping for a fixed duration.

## Interview recap

Be able to name the test boundary, why its dependencies are mocked or real, and which failure mode the test covers.
