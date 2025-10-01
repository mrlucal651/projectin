package com.neurofleetx.service;

import com.neurofleetx.model.MaintenanceLog;
import com.neurofleetx.model.Vehicle;
import com.neurofleetx.model.VehicleHealthMetrics;
import com.neurofleetx.repository.MaintenanceLogRepository;
import com.neurofleetx.repository.VehicleHealthMetricsRepository;
import com.neurofleetx.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class MaintenanceService {
    
    @Autowired
    private MaintenanceLogRepository maintenanceLogRepository;
    
    @Autowired
    private VehicleHealthMetricsRepository healthMetricsRepository;
    
    @Autowired
    private VehicleRepository vehicleRepository;

    private final Random random = new Random();

    // Maintenance Log Operations
    public List<MaintenanceLog> getAllMaintenanceLogs() {
        return maintenanceLogRepository.findAll();
    }

    public List<MaintenanceLog> getMaintenanceLogsByVehicle(Long vehicleId) {
        return maintenanceLogRepository.findByVehicleIdOrderByRecordedAtDesc(vehicleId);
    }

    public List<MaintenanceLog> getActiveAlerts() {
        return maintenanceLogRepository.findActiveAlertsOrderByRecordedAtDesc();
    }

    public List<MaintenanceLog> getAlertsBySeverity(MaintenanceLog.AlertSeverity severity) {
        return maintenanceLogRepository.findByAlertSeverity(severity);
    }

    public MaintenanceLog createMaintenanceLog(MaintenanceLog log) {
        // Check if alert should be triggered based on metric value
        checkAndTriggerAlert(log);
        return maintenanceLogRepository.save(log);
    }

    // Vehicle Health Metrics Operations
    public List<VehicleHealthMetrics> getAllVehicleHealthMetrics() {
        return healthMetricsRepository.findAll();
    }

    public Optional<VehicleHealthMetrics> getVehicleHealthMetrics(Long vehicleId) {
        return healthMetricsRepository.findByVehicleId(vehicleId);
    }

    public VehicleHealthMetrics updateVehicleHealthMetrics(Long vehicleId, VehicleHealthMetrics metrics) {
        Optional<Vehicle> vehicleOpt = vehicleRepository.findById(vehicleId);
        if (vehicleOpt.isPresent()) {
            metrics.setVehicle(vehicleOpt.get());
            metrics.setLastUpdated(LocalDateTime.now());
            
            // Calculate overall health score
            calculateOverallHealthScore(metrics);
            
            // Generate maintenance logs for critical metrics
            generateMaintenanceLogsFromMetrics(metrics);
            
            return healthMetricsRepository.save(metrics);
        }
        throw new RuntimeException("Vehicle not found: " + vehicleId);
    }

    public VehicleHealthMetrics simulateVehicleHealthMetrics(Long vehicleId) {
        Optional<Vehicle> vehicleOpt = vehicleRepository.findById(vehicleId);
        if (vehicleOpt.isPresent()) {
            Vehicle vehicle = vehicleOpt.get();
            
            // Get existing metrics or create new ones
            VehicleHealthMetrics metrics = healthMetricsRepository.findByVehicleId(vehicleId)
                    .orElse(new VehicleHealthMetrics(vehicle));
            
            // Simulate realistic metrics
            simulateRealisticMetrics(metrics);
            
            // Calculate health score and status
            calculateOverallHealthScore(metrics);
            
            // Generate maintenance logs for any alerts
            generateMaintenanceLogsFromMetrics(metrics);
            
            return healthMetricsRepository.save(metrics);
        }
        throw new RuntimeException("Vehicle not found: " + vehicleId);
    }

    public void simulateAllVehicleMetrics() {
        List<Vehicle> vehicles = vehicleRepository.findAll();
        for (Vehicle vehicle : vehicles) {
            simulateVehicleHealthMetrics(vehicle.getId());
        }
    }

    public Double getAverageFleetHealthScore() {
        Double average = healthMetricsRepository.getAverageFleetHealthScore();
        return average != null ? average : 0.0;
    }

    public List<VehicleHealthMetrics> getVehiclesNeedingMaintenance() {
        return healthMetricsRepository.findByOverallHealthScoreLessThan(70.0);
    }

    // Private helper methods
    private void checkAndTriggerAlert(MaintenanceLog log) {
        boolean alertTriggered = false;
        String alertMessage = "";
        MaintenanceLog.AlertSeverity severity = MaintenanceLog.AlertSeverity.LOW;

        switch (log.getMetricType()) {
            case ENGINE_TEMPERATURE:
                if (log.getValue() > 100) {
                    alertTriggered = true;
                    severity = log.getValue() > 110 ? MaintenanceLog.AlertSeverity.CRITICAL : MaintenanceLog.AlertSeverity.HIGH;
                    alertMessage = "Engine temperature critical: " + log.getValue() + "°C";
                }
                break;
            case TIRE_PRESSURE:
                if (log.getValue() < 30 || log.getValue() > 40) {
                    alertTriggered = true;
                    severity = log.getValue() < 25 ? MaintenanceLog.AlertSeverity.HIGH : MaintenanceLog.AlertSeverity.MEDIUM;
                    alertMessage = "Tire pressure abnormal: " + log.getValue() + " PSI";
                }
                break;
            case FUEL_LEVEL:
                if (log.getValue() < 15) {
                    alertTriggered = true;
                    severity = log.getValue() < 5 ? MaintenanceLog.AlertSeverity.HIGH : MaintenanceLog.AlertSeverity.MEDIUM;
                    alertMessage = "Low fuel level: " + log.getValue() + "%";
                }
                break;
            case BATTERY_VOLTAGE:
                if (log.getValue() < 12.0 || log.getValue() > 14.5) {
                    alertTriggered = true;
                    severity = log.getValue() < 11.5 ? MaintenanceLog.AlertSeverity.HIGH : MaintenanceLog.AlertSeverity.MEDIUM;
                    alertMessage = "Battery voltage abnormal: " + log.getValue() + "V";
                }
                break;
            case OIL_PRESSURE:
                if (log.getValue() < 20) {
                    alertTriggered = true;
                    severity = log.getValue() < 10 ? MaintenanceLog.AlertSeverity.CRITICAL : MaintenanceLog.AlertSeverity.HIGH;
                    alertMessage = "Low oil pressure: " + log.getValue() + " PSI";
                }
                break;
        }

        log.setAlert(alertTriggered);
        if (alertTriggered) {
            log.setAlertSeverity(severity);
            log.setAlertMessage(alertMessage);
        }
    }

    private void simulateRealisticMetrics(VehicleHealthMetrics metrics) {
        // Engine metrics
        metrics.setEngineTemperature(85.0 + (random.nextGaussian() * 10)); // Normal: 85°C ± 10
        metrics.setOilPressure(30.0 + (random.nextGaussian() * 8)); // Normal: 30 PSI ± 8
        metrics.setCoolantLevel(80.0 + (random.nextGaussian() * 15)); // Normal: 80% ± 15

        // Tire pressure (Normal: 32-35 PSI)
        metrics.setFrontLeftTirePressure(33.0 + (random.nextGaussian() * 3));
        metrics.setFrontRightTirePressure(33.0 + (random.nextGaussian() * 3));
        metrics.setRearLeftTirePressure(33.0 + (random.nextGaussian() * 3));
        metrics.setRearRightTirePressure(33.0 + (random.nextGaussian() * 3));

        // Fuel and battery
        metrics.setFuelLevel(Math.max(5, 70.0 + (random.nextGaussian() * 25))); // 5-95%
        metrics.setBatteryVoltage(12.6 + (random.nextGaussian() * 0.5)); // Normal: 12.6V ± 0.5

        // Other metrics
        metrics.setTransmissionTemperature(75.0 + (random.nextGaussian() * 8));
        metrics.setBrakeFluidLevel(85.0 + (random.nextGaussian() * 10));
        metrics.setVibrationLevel(0.5 + (random.nextGaussian() * 0.3));
        metrics.setMileage(50000 + random.nextInt(100000));

        metrics.setLastUpdated(LocalDateTime.now());
    }

    private void calculateOverallHealthScore(VehicleHealthMetrics metrics) {
        double score = 100.0;
        
        // Engine temperature (0-20 points)
        if (metrics.getEngineTemperature() != null) {
            if (metrics.getEngineTemperature() > 100) score -= 20;
            else if (metrics.getEngineTemperature() > 95) score -= 10;
        }
        
        // Tire pressure (0-15 points)
        double avgTirePressure = (metrics.getFrontLeftTirePressure() + metrics.getFrontRightTirePressure() + 
                                 metrics.getRearLeftTirePressure() + metrics.getRearRightTirePressure()) / 4.0;
        if (avgTirePressure < 30 || avgTirePressure > 40) score -= 15;
        else if (avgTirePressure < 32 || avgTirePressure > 36) score -= 8;
        
        // Fuel level (0-10 points)
        if (metrics.getFuelLevel() != null) {
            if (metrics.getFuelLevel() < 10) score -= 10;
            else if (metrics.getFuelLevel() < 20) score -= 5;
        }
        
        // Battery voltage (0-15 points)
        if (metrics.getBatteryVoltage() != null) {
            if (metrics.getBatteryVoltage() < 12.0 || metrics.getBatteryVoltage() > 14.5) score -= 15;
            else if (metrics.getBatteryVoltage() < 12.3 || metrics.getBatteryVoltage() > 13.5) score -= 8;
        }
        
        // Oil pressure (0-20 points)
        if (metrics.getOilPressure() != null) {
            if (metrics.getOilPressure() < 15) score -= 20;
            else if (metrics.getOilPressure() < 25) score -= 10;
        }
        
        // Other factors (0-20 points)
        if (metrics.getCoolantLevel() != null && metrics.getCoolantLevel() < 50) score -= 10;
        if (metrics.getBrakeFluidLevel() != null && metrics.getBrakeFluidLevel() < 60) score -= 10;
        
        score = Math.max(0, Math.min(100, score));
        metrics.setOverallHealthScore(score);
        
        // Set health status based on score
        if (score >= 90) metrics.setHealthStatus(VehicleHealthMetrics.HealthStatus.EXCELLENT);
        else if (score >= 75) metrics.setHealthStatus(VehicleHealthMetrics.HealthStatus.GOOD);
        else if (score >= 60) metrics.setHealthStatus(VehicleHealthMetrics.HealthStatus.FAIR);
        else if (score >= 40) metrics.setHealthStatus(VehicleHealthMetrics.HealthStatus.POOR);
        else metrics.setHealthStatus(VehicleHealthMetrics.HealthStatus.CRITICAL);
    }

    private void generateMaintenanceLogsFromMetrics(VehicleHealthMetrics metrics) {
        Vehicle vehicle = metrics.getVehicle();
        
        // Engine temperature log
        if (metrics.getEngineTemperature() != null) {
            MaintenanceLog tempLog = new MaintenanceLog(vehicle, MaintenanceLog.MetricType.ENGINE_TEMPERATURE, 
                                                       metrics.getEngineTemperature());
            checkAndTriggerAlert(tempLog);
            if (tempLog.getAlert()) {
                maintenanceLogRepository.save(tempLog);
            }
        }
        
        // Tire pressure logs
        double avgTirePressure = (metrics.getFrontLeftTirePressure() + metrics.getFrontRightTirePressure() + 
                                 metrics.getRearLeftTirePressure() + metrics.getRearRightTirePressure()) / 4.0;
        MaintenanceLog tireLog = new MaintenanceLog(vehicle, MaintenanceLog.MetricType.TIRE_PRESSURE, avgTirePressure);
        checkAndTriggerAlert(tireLog);
        if (tireLog.getAlert()) {
            maintenanceLogRepository.save(tireLog);
        }
        
        // Fuel level log
        if (metrics.getFuelLevel() != null) {
            MaintenanceLog fuelLog = new MaintenanceLog(vehicle, MaintenanceLog.MetricType.FUEL_LEVEL, 
                                                       metrics.getFuelLevel());
            checkAndTriggerAlert(fuelLog);
            if (fuelLog.getAlert()) {
                maintenanceLogRepository.save(fuelLog);
            }
        }
        
        // Battery voltage log
        if (metrics.getBatteryVoltage() != null) {
            MaintenanceLog batteryLog = new MaintenanceLog(vehicle, MaintenanceLog.MetricType.BATTERY_VOLTAGE, 
                                                          metrics.getBatteryVoltage());
            checkAndTriggerAlert(batteryLog);
            if (batteryLog.getAlert()) {
                maintenanceLogRepository.save(batteryLog);
            }
        }
        
        // Oil pressure log
        if (metrics.getOilPressure() != null) {
            MaintenanceLog oilLog = new MaintenanceLog(vehicle, MaintenanceLog.MetricType.OIL_PRESSURE, 
                                                      metrics.getOilPressure());
            checkAndTriggerAlert(oilLog);
            if (oilLog.getAlert()) {
                maintenanceLogRepository.save(oilLog);
            }
        }
    }
}