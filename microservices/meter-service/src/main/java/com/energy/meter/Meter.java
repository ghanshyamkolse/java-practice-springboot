package com.energy.meter;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "meters")
public class Meter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "meter_id", nullable = false, unique = true)
    private String meterId;

    @Column(nullable = false)
    private String status;

    protected Meter() {
    }

    public Meter(String meterId, String status) {
        this.meterId = meterId;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public String getMeterId() {
        return meterId;
    }

    public String getStatus() {
        return status;
    }
}
