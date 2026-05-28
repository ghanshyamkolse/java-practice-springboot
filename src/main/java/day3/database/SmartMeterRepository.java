package day3.database;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA Repository showing patterns to eliminate N+1 Query scenarios.
 */
@Repository
public interface SmartMeterRepository extends JpaRepository<SmartMeter, Long> {

    /**
     * Solution 1: JPQL JOIN FETCH
     * Forces a single SQL SELECT query with an inner/left join to load the readings collection.
     */
    @Query("SELECT m FROM SmartMeter m LEFT JOIN FETCH m.readings")
    List<SmartMeter> findAllUsingJoinFetch();

    /**
     * Solution 2: Entity Graph
     * Spring Data overrides the default Lazy fetch behavior dynamically to fetch readings in 1 query.
     */
    @EntityGraph(attributePaths = {"readings"})
    @Query("SELECT m FROM SmartMeter m")
    List<SmartMeter> findAllUsingEntityGraph();
}
