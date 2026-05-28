package day5.tdd;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.*;

import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;

/**
 * Integration test demonstrating HTTP service mocking via WireMock.
 * Spins up an in-memory HTTP server on port 8089 to stub downstream API responses.
 */
@WireMockTest(httpPort = 8089)
public class DownstreamApiTest {

    private final RestTemplate restTemplate = new RestTemplate();

    @Test
    public void testFetchExternalMeterTariff() {
        // 1. Arrange: Stub out HTTP GET request
        stubFor(get(urlEqualTo("/api/tariffs/M101"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withStatus(200)
                        .withBody("{\"tariffCode\":\"OFFPEAK\",\"rate\":0.12}")));

        // 2. Act: Call stubbed WireMock endpoint using REST Template
        String targetUrl = "http://localhost:8089/api/tariffs/M101";
        String rawJson = restTemplate.getForObject(targetUrl, String.class);

        // 3. Assert: Verify response fields
        assertNotNull(rawJson);
        assertTrue(rawJson.contains("OFFPEAK"));
        assertTrue(rawJson.contains("0.12"));
    }
}
