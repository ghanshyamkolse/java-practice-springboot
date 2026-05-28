package day5.tdd;

import java.util.Optional;

/**
 * Interface to mock inside our unit tests.
 */
public interface MeterRepository {
    Optional<SmartMeterMock> findByMeterId(String meterId);
}

class SmartMeterMock {
    private String meterId;
    private String status;

    public SmartMeterMock(String meterId, String status) {
        this.meterId = meterId;
        this.status = status;
    }

    public String getMeterId() { return meterId; }
    public String getStatus() { return status; }
}
