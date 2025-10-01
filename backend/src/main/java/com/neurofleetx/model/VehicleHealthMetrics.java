package com.neurofleetx.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "vehicle_health_metrics")
public class VehicleHealthMetrics {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;

    // Engine Metrics
    private Double engineTemperature; // Celsius
    private Double oilPressure; // PSI
    private Double coolantLevel; // Percentage

    // Tire Metrics
    private Double frontLeftTirePressure; // PSI
    private Double frontRightTirePressure; // PSI
    private Double rearLeftTirePressure; // PSI
    private Double rearRightTirePressure; // PSI

    // Fuel & Battery
    private Double fuelLevel; // Percentage
    private Double batteryVoltage; // Volts

    // Transmission & Brakes
    private Double transmissionTemperature; // Celsius
    private Double brakeFluidLevel; // Percentage

    // Performance Metrics
    private Double vibrationLevel; // G-force
    private Integer mileage; // Total kilometers

    // Health Score (0-100)
    private Double overallHealthScore;

    @Enumerated(EnumType.STRING)
    private HealthStatus healthStatus;

    @Column(name = "last_updated")
    private LocalDateTime lastUpdated = LocalDateTime.now();

    // Constructors
    public VehicleHealthMetrics() {}

    public VehicleHealthMetrics(Vehicle vehicle) {
        this.vehicle = vehicle;
        this.healthStatus = HealthStatus.GOOD;
        this.overallHealthScore = 85.0;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Vehicle getVehicle() { return vehicle; }
    public void setVehicle(Vehicle vehicle) { this.vehicle = vehicle; }

    public Double getEngineTemperature() { return engineTemperature; }
    public void setEngineTemperature(Double engineTemperature) { this.engineTemperature = engineTemperature; }

    public Double getOilPressure() { return oilPressure; }
    public void setOilPressure(Double oilPressure) { this.oilPressure = oilPressure; }

    public Double getCoolantLevel() { return coolantLevel; }
    public void setCoolantLevel(Double coolantLevel) { this.coolantLevel = coolantLevel; }

    public Double getFrontLeftTirePressure() { return frontLeftTirePressure; }
    public void setFrontLeftTirePressure(Double frontLeftTirePressure) { this.frontLeftTirePressure = frontLeftTirePressure; }

    public Double getFrontRightTirePressure() { return frontRightTirePressure; }
    public void setFrontRightTirePressure(Double frontRightTirePressure) { this.frontRightTirePressure = frontRightTirePressure; }

    public Double getRearLeftTirePressure() { return rearLeftTirePressure; }
    public void setRearLeftTirePressure(Double rearLeftTirePressure) { this.rearLeftTirePressure = rearLeftTirePressure; }

    public Double getRearRightTirePressure() { return rearRightTirePressure; }
    public void setRearRightTirePressure(Double rearRightTirePressure) { this.rearRightTirePressure = rearRightTirePressure; }

    public Double getFuelLevel() { return fuelLevel; }
    public void setFuelLevel(Double fuelLevel) { this.fuelLevel = fuelLevel; }

    public Double getBatteryVoltage() { return batteryVoltage; }
    public void setBatteryVoltage(Double batteryVoltage) { this.batteryVoltage = batteryVoltage; }

    public Double getTransmissionTemperature() { return transmissionTemperature; }
    public void setTransmissionTemperature(Double transmissionTemperature) { this.transmissionTemperature = transmissionTemperature; }

    public Double getBrakeFluidLevel() { return brakeFluidLevel; }
    public void setBrakeFluidLevel(Double brakeFluidLevel) { this.brakeFluidLevel = brakeFluidLevel; }

    public Double getVibrationLevel() { return vibrationLevel; }
    public void setVibrationLevel(Double vibrationLevel) { this.vibrationLevel = vibrationLevel; }

    public Integer getMileage() { return mileage; }
    public void setMileage(Integer mileage) { this.mileage = mileage; }

    public Double getOverallHealthScore() { return overallHealthScore; }
    public void setOverallHealthScore(Double overallHealthScore) { this.overallHealthScore = overallHealthScore; }

    public HealthStatus getHealthStatus() { return healthStatus; }
    public void setHealthStatus(HealthStatus healthStatus) { this.healthStatus = healthStatus; }

    public LocalDateTime getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(LocalDateTime lastUpdated) { this.lastUpdated = lastUpdated; }

    // Enum
    public enum HealthStatus {
        EXCELLENT, GOOD, FAIR, POOR, CRITICAL
    }
}