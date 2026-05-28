package day4.javaspring;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * Showcase class compiling core modern Java features from Java 8 through Java 17.
 */
public class ModernJavaShowcase {

    // ==========================================
    // 1. Java 17 Records (Immutable Data Carriers)
    // ==========================================
    public record TelemetryRecord(String meterId, double activePower, LocalDateTime timestamp) {}

    // ==========================================
    // 2. Java 17 Sealed Classes (Hierarchy Limits)
    // ==========================================
    public sealed interface Tariff permits FixedTariff, DynamicTariff {}
    public static final class FixedTariff implements Tariff {}
    public static final class DynamicTariff implements Tariff {}

    public static void main(String[] args) throws Exception {
        ModernJavaShowcase showcase = new ModernJavaShowcase();

        // 3. Java 11 Local Variable Syntax (var)
        var records = List.of(
                new TelemetryRecord("M101", 142.50, LocalDateTime.now()),
                new TelemetryRecord("M102", 95.20, LocalDateTime.now()),
                new TelemetryRecord("M103", 220.10, LocalDateTime.now())
        );

        // 4. Java 8 Stream API & Lambdas
        List<String> highUsageMeters = records.stream()
                .filter(r -> r.activePower() > 100.0)
                .map(TelemetryRecord::meterId)
                .collect(Collectors.toList());

        System.out.println("High consumption meters: " + highUsageMeters);

        // 5. Java 8 Optional API
        Optional<TelemetryRecord> optionalRecord = records.stream().findFirst();
        String meterId = optionalRecord.map(TelemetryRecord::meterId).orElse("UNKNOWN");
        System.out.println("Resolved Meter ID: " + meterId);

        // 6. Java 17 Text Blocks
        String mockJsonResponse = """
            {
                "meterId": "M101",
                "status": "ACTIVE",
                "hardwareVersion": "v2.1"
            }
            """;
        System.out.println("JSON Text Block:\n" + mockJsonResponse);

        // 7. Java 17 Pattern Matching for instanceof
        Object tariff = new FixedTariff();
        if (tariff instanceof FixedTariff f) {
            System.out.println("Resolved class: FixedTariff type verified.");
        }

        // 8. Java 8/11 CompletableFuture & HTTP Client
        showcase.fetchTariffAsync();
    }

    /**
     * Executes non-blocking HTTP requests asynchronously.
     */
    public void fetchTariffAsync() {
        var client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();

        var request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.github.com")) // Dummy test endpoint
                .GET()
                .build();

        // Asynchronous non-blocking HTTP request processing
        CompletableFuture<HttpResponse<String>> futureResponse = client.sendAsync(request, HttpResponse.BodyHandlers.ofString());

        futureResponse.thenApply(HttpResponse::body)
                .thenAccept(body -> System.out.println("Async Request completed. Response size: " + body.length()))
                .exceptionally(ex -> {
                    System.err.println("API error occurred: " + ex.getMessage());
                    return null;
                });
    }
}
