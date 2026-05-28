package day3.database;

import jakarta.persistence.*;
import org.hibernate.annotations.BatchSize;

import java.util.ArrayList;
import java.util.List;

/**
 * JPA Entity representing a Smart Meter in the SQL Server database.
 * Demonstrates clean mapping, foreign keys, and batch sizing for performance optimization.
 */
@Entity
@Table(name = "smart_meters")
public class SmartMeter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "meter_id", unique = true, nullable = false)
    private String meterId;

    @Column(name = "status")
    private String status;

    /**
     * Map meter readings.
     * BatchSize instructs Hibernate to load readings in batches rather than individually,
     * mitigating the N+1 select issue when collections are traversed.
     */
    @OneToMany(mappedBy = "smartMeter", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @BatchSize(size = 20)
    private List<MeterReading> readings = new ArrayList<>();

    public SmartMeter() {}

    public SmartMeter(String meterId, String status) {
        this.meterId = meterId;
        this.status = status;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getMeterId() { return meterId; }
    public void setMeterId(String meterId) { this.meterId = meterId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public List<MeterReading> getReadings() { return readings; }
    public void setReadings(List<MeterReading> readings) { this.readings = readings; }
}
