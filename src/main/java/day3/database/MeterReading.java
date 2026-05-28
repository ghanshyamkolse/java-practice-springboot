package day3.database;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * JPA Entity representing an individual smart meter reading in SQL Server.
 */
@Entity
@Table(name = "meter_readings")
public class MeterReading {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "reading_value", nullable = false)
    private Double readingValue;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "smart_meter_id", nullable = false)
    private SmartMeter smartMeter;

    public MeterReading() {}

    public MeterReading(Double readingValue, LocalDateTime timestamp, SmartMeter smartMeter) {
        this.readingValue = readingValue;
        this.timestamp = timestamp;
        this.smartMeter = smartMeter;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Double getReadingValue() { return readingValue; }
    public void setReadingValue(Double readingValue) { this.readingValue = readingValue; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public SmartMeter getSmartMeter() { return smartMeter; }
    public void setSmartMeter(SmartMeter smartMeter) { this.smartMeter = smartMeter; }
}
