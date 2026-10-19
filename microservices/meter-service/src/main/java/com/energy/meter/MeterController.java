package com.energy.meter;

import java.net.URI;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;

@RestController
@RequestMapping("/api/meters")
public class MeterController {

    private static final Logger logger = LoggerFactory.getLogger(MeterController.class);

    private final MeterRepository meterRepository;

    public MeterController(MeterRepository meterRepository) {
        this.meterRepository = meterRepository;
    }

    @GetMapping
    public List<MeterResponse> getMeters() {
        List<MeterResponse> meters = meterRepository.findAll().stream().map(MeterResponse::from).toList();
        logger.info("Listed {} meters", meters.size());
        return meters;
    }

    @GetMapping("/{meterId}")
    public MeterResponse getMeter(@PathVariable String meterId) {
        return meterRepository.findByMeterId(meterId)
                .map(MeterResponse::from)
                .orElseThrow(() -> {
                    logger.warn("Meter not found: {}", meterId);
                    return new ResponseStatusException(NOT_FOUND, "Meter not found");
                });
    }

    @PostMapping
    public ResponseEntity<MeterResponse> createMeter(@jakarta.validation.Valid @RequestBody MeterRequest request) {
        if (meterRepository.existsByMeterId(request.meterId())) {
            logger.warn("Meter already exists: {}", request.meterId());
            throw new ResponseStatusException(CONFLICT, "Meter already exists");
        }

        Meter saved = meterRepository.save(new Meter(request.meterId(), request.status()));
        logger.info("Registered meter {}", saved.getMeterId());
        URI location = fromCurrentRequest().path("/{meterId}").buildAndExpand(saved.getMeterId()).toUri();
        return ResponseEntity.created(location).body(MeterResponse.from(saved));
    }
}
