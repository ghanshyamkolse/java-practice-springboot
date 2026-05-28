package day5.tdd;

import java.util.Optional;

/**
 * Service to execute business logic.
 */
public class MeterService {

    private final MeterRepository meterRepository;

    public MeterService(MeterRepository meterRepository) {
        this.meterRepository = meterRepository;
    }

    /**
     * Retrieves the status of a smart meter.
     * Throws an IllegalArgumentException if the meter is not registered.
     */
    public String getMeterStatus(String meterId) {
        Optional<SmartMeterMock> meter = meterRepository.findByMeterId(meterId);
        return meter.map(SmartMeterMock::getStatus)
                .orElseThrow(() -> new IllegalArgumentException("Meter not registered."));
    }
}
