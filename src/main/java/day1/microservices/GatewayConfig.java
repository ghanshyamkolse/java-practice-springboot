package day1.microservices;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Objects;

/**
 * Spring Cloud Gateway configuration to handle rate limiting.
 * It resolves the client's rate-limiting key by extracting the 'X-User-Id' header,
 * falling back to the remote IP address if the header is not present.
 */
@Configuration
public class GatewayConfig {

    @Bean
    public KeyResolver userKeyResolver() {
        return exchange -> {
            // Retrieve User ID header
            String userId = exchange.getRequest().getHeaders().getFirst("X-User-Id");
            if (userId != null && !userId.trim().isEmpty()) {
                return Mono.just(userId);
            }
            
            // Fallback to client IP address
            return Mono.just(
                Objects.requireNonNull(exchange.getRequest().getRemoteAddress())
                    .getAddress()
                    .getHostAddress()
            );
        };
    }
}
