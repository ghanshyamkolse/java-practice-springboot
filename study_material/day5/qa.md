# Day 5 Q&A: Testing and Agile Delivery

### Q1: When would you use `@WebMvcTest` instead of `@SpringBootTest`?

**Answer:** `@WebMvcTest` loads the web layer to test routing, JSON conversion, validation, and response codes without starting the database and every application component. `@SpringBootTest` starts a broader context and is appropriate when the behavior depends on several real components. Use the narrowest boundary that proves the requirement.

### Q2: How do you test an outgoing HTTP call?

**Answer:** Unit-test the caller's decision logic with a mock. Separately test the actual HTTP client against a controlled server such as WireMock to verify URI, headers, serialization, timeouts, and error mapping. Avoid calls to a live production dependency in tests.

### Q3: How would you test that a reading cannot be created for an unknown meter?

**Answer:** In a controller-slice test, configure the `MeterClient` to report a not-found response and assert the API returns `404` with the intended error shape. Also assert the reading repository was not called. An integration test can additionally verify the real client maps the meter service's HTTP response correctly.

### Q4: What does an end-to-end test prove that a controller test does not?

**Answer:** It exercises the deployed route across the gateway, service discovery, container networking, and real service HTTP calls. A controller-slice test is faster and more focused, but does not prove that Compose and service discovery are wired correctly.

### Q5: Explain red-green-refactor.

**Answer:** Write a failing test for the behavior, implement the smallest change that passes, then improve the design while keeping the test green. The test should describe externally meaningful behavior, including failure cases.

### Q6: How do you respond when estimates differ during refinement?

**Answer:** Ask people with high and low estimates to explain their assumptions and unknowns. The difference often reveals missing work, risk, or ambiguity. Clarify acceptance criteria and dependencies, then estimate again as a team rather than averaging unrelated numbers.
