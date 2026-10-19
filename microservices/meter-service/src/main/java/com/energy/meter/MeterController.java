package com.energy.meter;

import java.net.URI;
import java.util.List;
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

    private final MeterRepository meterRepository;

    public MeterController(MeterRepository meterRepository) {
        this.meterRepository = meterRepository;
    }

    @GetMapping
    public List<MeterResponse> getMeters() {
        return meterRepository.findAll().stream().map(MeterResponse::from).toList();
    }

    @GetMapping("/{meterId}")
    public MeterResponse getMeter(@PathVariable String meterId) {
        return meterRepository.findByMeterId(meterId)
                .map(MeterResponse::from)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Meter not found"));
    }

    @PostMapping
    public ResponseEntity<MeterResponse> createMeter(@jakarta.validation.Valid @RequestBody MeterRequest request) {
        if (meterRepository.existsByMeterId(request.meterId())) {
            throw new ResponseStatusException(CONFLICT, "Meter already exists");
        }

        Meter saved = meterRepository.save(new Meter(request.meterId(), request.status()));
        URI location = fromCurrentRequest().path("/{meterId}").buildAndExpand(saved.getMeterId()).toUri();
        return ResponseEntity.created(location).body(MeterResponse.from(saved));
    }
}
