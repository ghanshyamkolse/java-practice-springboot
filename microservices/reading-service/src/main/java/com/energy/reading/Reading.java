package com.energy.reading;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "readings", indexes = @Index(name = "idx_readings_meter_id", columnList = "meter_id"))
public class Reading {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "meter_id", nullable = false)
    private String meterId;

    @Column(name = "reading_value", nullable = false)
    private double value;

    @Column(name = "recorded_at", nullable = false)
    private Instant recordedAt;

    protected Reading() {
    }

    public Reading(String meterId, double value, Instant recordedAt) {
        this.meterId = meterId;
        this.value = value;
        this.recordedAt = recordedAt;
    }

    public Long getId() {
        return id;
    }

    public String getMeterId() {
        return meterId;
    }

    public double getValue() {
        return value;
    }

    public Instant getRecordedAt() {
        return recordedAt;
    }
}
