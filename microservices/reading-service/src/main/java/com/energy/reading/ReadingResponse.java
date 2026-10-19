package com.energy.reading;

import java.time.Instant;

public record ReadingResponse(Long id, String meterId, double value, Instant recordedAt) {

    static ReadingResponse from(Reading reading) {
        return new ReadingResponse(reading.getId(), reading.getMeterId(), reading.getValue(), reading.getRecordedAt());
    }
}
