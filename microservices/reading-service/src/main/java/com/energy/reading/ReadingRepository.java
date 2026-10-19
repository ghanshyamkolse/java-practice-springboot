package com.energy.reading;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReadingRepository extends JpaRepository<Reading, Long> {

    List<Reading> findByMeterIdOrderByRecordedAtDesc(String meterId);
}
