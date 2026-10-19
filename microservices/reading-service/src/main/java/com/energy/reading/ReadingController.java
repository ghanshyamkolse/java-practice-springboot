package com.energy.reading;

import java.time.Instant;
import java.util.List;
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

    private final ReadingRepository readingRepository;
    private final MeterClient meterClient;

    public ReadingController(ReadingRepository readingRepository, MeterClient meterClient) {
        this.readingRepository = readingRepository;
        this.meterClient = meterClient;
    }

    @GetMapping("/meter/{meterId}")
    public List<ReadingResponse> getReadings(@PathVariable String meterId) {
        return readingRepository.findByMeterIdOrderByRecordedAtDesc(meterId)
                .stream()
                .map(ReadingResponse::from)
                .toList();
    }

    @PostMapping
    public ResponseEntity<ReadingResponse> createReading(@jakarta.validation.Valid @RequestBody ReadingRequest request) {
        try {
            meterClient.requireRegisteredMeter(request.meterId());
        } catch (org.springframework.web.client.HttpClientErrorException.NotFound exception) {
            throw new MeterNotFoundException(request.meterId());
        }

        Reading saved = readingRepository.save(new Reading(request.meterId(), request.value(), Instant.now()));
        return ResponseEntity.status(HttpStatus.CREATED).body(ReadingResponse.from(saved));
    }
}
