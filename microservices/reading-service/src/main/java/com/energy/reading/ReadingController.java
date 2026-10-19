package com.energy.reading;

import java.time.Instant;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
@RequestMapping("/api/readings")
public class ReadingController {

    private static final Logger logger = LoggerFactory.getLogger(ReadingController.class);

    private final ReadingRepository readingRepository;
    private final MeterClient meterClient;

    public ReadingController(ReadingRepository readingRepository, MeterClient meterClient) {
        this.readingRepository = readingRepository;
        this.meterClient = meterClient;
    }

    @GetMapping("/meter/{meterId}")
    public List<ReadingResponse> getReadings(@PathVariable String meterId) {
        List<ReadingResponse> readings = readingRepository.findByMeterIdOrderByRecordedAtDesc(meterId)
                .stream()
                .map(ReadingResponse::from)
                .toList();
        logger.info("Listed {} readings for meter {}", readings.size(), meterId);
        return readings;
    }

    @PostMapping
    public ResponseEntity<ReadingResponse> createReading(@jakarta.validation.Valid @RequestBody ReadingRequest request) {
        try {
            meterClient.requireRegisteredMeter(request.meterId());
        } catch (org.springframework.web.client.HttpClientErrorException.NotFound exception) {
            logger.warn("Cannot record reading for unregistered meter {}", request.meterId());
            throw new MeterNotFoundException(request.meterId());
        }

        Reading saved = readingRepository.save(new Reading(request.meterId(), request.value(), Instant.now()));
        logger.info("Recorded reading for meter {}", saved.getMeterId());
        return ResponseEntity.status(HttpStatus.CREATED).body(ReadingResponse.from(saved));
    }
}
