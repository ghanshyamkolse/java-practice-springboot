# Day 1 Q&A: Microservices, RESTful & SOAP Web Services

### Q1: How do you handle distributed transactions in a Microservice architecture?
> **Answer:** 
> We implement the **Saga Pattern** rather than traditional 2-Phase Commit (2PC) since 2PC blocks database resources and does not scale well in high-volume microservices. 
> In a Saga, we split the transaction into multiple steps:
> 1. Each microservice executes a local transaction and publishes an event or message.
> 2. The next microservice listens to that event and executes its local transaction.
> 3. If a step fails, the Saga executes **compensating transactions** in reverse order to roll back the changes programmatically (e.g., if inventory reservation fails, we refund the payment).

---

### Q2: If a legacy service communicates via SOAP XML, how do you consume it in a Spring Boot app?
> **Answer:**
> I consume it using **Spring Web Services (Spring WS)**. 
> 1. Add `jaxb2-maven-plugin` or `cxf-codegen-plugin` in `pom.xml` to generate Java representation classes from the remote `.wsdl` file.
> 2. Configure a `Jaxb2Marshaller` bean pointing to the package containing the generated classes.
> 3. Define a `WebServiceTemplate` bean and inject the marshaller/unmarshaller.
> 4. Inject `WebServiceTemplate` in the service class and call `.marshalSendAndReceive(requestPayload)` to communicate.

---

### Q3: What is the difference between a Filter and an Interceptor in Spring Cloud Gateway?
> **Answer:**
> * **Gateway Filter:** Applies to specific routes or all routes globally (GlobalFilter). It intercepts requests and responses entering and exiting the gateway. You write code using WebFlux's non-blocking `ServerWebExchange`.
> * **Spring MVC Interceptor (`HandlerInterceptor`):** Operates on the web framework level for servlet-based applications, dealing with HttpServletRequest and HttpServletResponse.
> * Since Spring Cloud Gateway is built on Spring WebFlux (reactive and non-blocking), it uses `GatewayFilter` instead of standard servlet filters or interceptors.

---

### Q4: How does a Circuit Breaker decide when to open or close?
> **Answer:**
> It uses a sliding window of recent calls (either count-based or time-based).
> 1. If the failure rate (e.g., timeouts, exceptions) exceeds the configured threshold (e.g., 50% failures out of the last 10 calls), the circuit transitions to **OPEN**.
> 2. In the **OPEN** state, calls fail fast immediately, executing a fallback method if provided, without hitting the target service.
> 3. After a configured wait duration, the circuit enters **HALF-OPEN** and permits a limited number of requests. If these succeed, it transitions back to **CLOSED**; if they fail, it goes back to **OPEN**.
