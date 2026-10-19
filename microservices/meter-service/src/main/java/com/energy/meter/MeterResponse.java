package com.energy.meter;

public record MeterResponse(Long id, String meterId, String status) {

    static MeterResponse from(Meter meter) {
        return new MeterResponse(meter.getId(), meter.getMeterId(), meter.getStatus());
    }
}
