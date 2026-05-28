package day6.scaling;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Spring Cache abstraction configuration integrating local Caffeine cache.
 * Optimizes read-heavy lookup configurations (e.g. meter active states, tariffs)
 * by caching them in-memory within the JVM heap space.
 */
@Configuration
@EnableCaching
public class CacheConfig {

    /**
     * Declares CaffeineCacheManager with custom configurations:
     * - Initial size capacity limit.
     * - Maximum cache size bounds to prevent OutOfMemoryErrors.
     * - Write-based TTL (Time-To-Live) eviction rules.
     */
    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        
        // Define cache regions
        cacheManager.setCacheNames(List.of("meterConfigs", "billingTariffs"));
        
        // Set Caffeine configuration policies
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .initialCapacity(100)
                .maximumSize(5000) // Limits total entries to 5000 in heap
                .expireAfterWrite(10, TimeUnit.MINUTES) // TTL eviction of 10 minutes
                .recordStats() // Collects cache hit/miss statistics for logging
        );
        
        return cacheManager;
    }
}
