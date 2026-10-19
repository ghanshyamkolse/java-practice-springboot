package com.energy.meter;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MeterRepository extends JpaRepository<Meter, Long> {

    Optional<Meter> findByMeterId(String meterId);

    boolean existsByMeterId(String meterId);
}
