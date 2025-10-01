package com.neurofleetx.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "maintenance_logs")
public class MaintenanceLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;

    @NotBlank
    @Enumerated(EnumType.STRING)
    private MetricType metricType;

    @NotNull
    private Double value;

    @NotNull
    private Boolean alert = false;

    @Enumerated(EnumType.STRING)
    private AlertSeverity alertSeverity;

    private String alertMessage;

    @Column(name = "recorded_at")
    private LocalDateTime recordedAt = LocalDateTime.now();

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    // Constructors
    public MaintenanceLog() {}

    public MaintenanceLog(Vehicle vehicle, MetricType metricType, Double value) {
        this.vehicle = vehicle;
        this.metricType = metricType;
        this.value = value;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Vehicle getVehicle() { return vehicle; }
    public void setVehicle(Vehicle vehicle) { this.vehicle = vehicle; }

    public MetricType getMetricType() { return metricType; }
    public void setMetricType(MetricType metricType) { this.metricType = metricType; }

    public Double getValue() { return value; }
    public void setValue(Double value) { this.value = value; }

    public Boolean getAlert() { return alert; }
    public void setAlert(Boolean alert) { this.alert = alert; }

    public AlertSeverity getAlertSeverity() { return alertSeverity; }
    public void setAlertSeverity(AlertSeverity alertSeverity) { this.alertSeverity = alertSeverity; }

    public String getAlertMessage() { return alertMessage; }
    public void setAlertMessage(String alertMessage) { this.alertMessage = alertMessage; }

    public LocalDateTime getRecordedAt() { return recordedAt; }
    public void setRecordedAt(LocalDateTime recordedAt) { this.recordedAt = recordedAt; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    // Enums
    public enum MetricType {
        ENGINE_TEMPERATURE,
        TIRE_PRESSURE,
        FUEL_LEVEL,
        BATTERY_VOLTAGE,
        OIL_PRESSURE,
        BRAKE_FLUID,
        COOLANT_LEVEL,
        TRANSMISSION_TEMP,
        MILEAGE,
        VIBRATION_LEVEL
    }

    public enum AlertSeverity {
        LOW, MEDIUM, HIGH, CRITICAL
    }
}