package com.energy.reading;

public class MeterNotFoundException extends RuntimeException {

    public MeterNotFoundException(String meterId) {
        super("Meter not found: " + meterId);
    }
}
