package com.neurofleetx.controller;

import com.neurofleetx.model.MaintenanceLog;
import com.neurofleetx.model.VehicleHealthMetrics;
import com.neurofleetx.service.MaintenanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/maintenance")
public class MaintenanceController {
    
    @Autowired
    private MaintenanceService maintenanceService;

    // Maintenance Logs Endpoints
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @GetMapping("/logs")
    public ResponseEntity<List<MaintenanceLog>> getAllMaintenanceLogs() {
        return ResponseEntity.ok(maintenanceService.getAllMaintenanceLogs());
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('DRIVER')")
    @GetMapping("/logs/vehicle/{vehicleId}")
    public ResponseEntity<List<MaintenanceLog>> getMaintenanceLogsByVehicle(@PathVariable Long vehicleId) {
        return ResponseEntity.ok(maintenanceService.getMaintenanceLogsByVehicle(vehicleId));
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @GetMapping("/alerts")
    public ResponseEntity<List<MaintenanceLog>> getActiveAlerts() {
        return ResponseEntity.ok(maintenanceService.getActiveAlerts());
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @GetMapping("/alerts/severity/{severity}")
    public ResponseEntity<List<MaintenanceLog>> getAlertsBySeverity(@PathVariable String severity) {
        try {
            MaintenanceLog.AlertSeverity alertSeverity = MaintenanceLog.AlertSeverity.valueOf(severity.toUpperCase());
            return ResponseEntity.ok(maintenanceService.getAlertsBySeverity(alertSeverity));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @PostMapping("/logs")
    public ResponseEntity<MaintenanceLog> createMaintenanceLog(@RequestBody MaintenanceLog log) {
        MaintenanceLog savedLog = maintenanceService.createMaintenanceLog(log);
        return ResponseEntity.ok(savedLog);
    }

    // Vehicle Health Metrics Endpoints
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @GetMapping("/health")
    public ResponseEntity<List<VehicleHealthMetrics>> getAllVehicleHealthMetrics() {
        return ResponseEntity.ok(maintenanceService.getAllVehicleHealthMetrics());
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('DRIVER')")
    @GetMapping("/health/vehicle/{vehicleId}")
    public ResponseEntity<VehicleHealthMetrics> getVehicleHealthMetrics(@PathVariable Long vehicleId) {
        return maintenanceService.getVehicleHealthMetrics(vehicleId)
                .map(metrics -> ResponseEntity.ok().body(metrics))
                .orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('DRIVER')")
    @PutMapping("/health/vehicle/{vehicleId}")
    public ResponseEntity<VehicleHealthMetrics> updateVehicleHealthMetrics(
            @PathVariable Long vehicleId, 
            @RequestBody VehicleHealthMetrics metrics) {
        try {
            VehicleHealthMetrics updatedMetrics = maintenanceService.updateVehicleHealthMetrics(vehicleId, metrics);
            return ResponseEntity.ok(updatedMetrics);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @PostMapping("/health/simulate/{vehicleId}")
    public ResponseEntity<VehicleHealthMetrics> simulateVehicleHealthMetrics(@PathVariable Long vehicleId) {
        try {
            VehicleHealthMetrics metrics = maintenanceService.simulateVehicleHealthMetrics(vehicleId);
            return ResponseEntity.ok(metrics);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @PostMapping("/health/simulate-all")
    public ResponseEntity<Map<String, String>> simulateAllVehicleMetrics() {
        maintenanceService.simulateAllVehicleMetrics();
        Map<String, String> response = new HashMap<>();
        response.put("message", "Health metrics simulated for all vehicles");
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @GetMapping("/health/needing-maintenance")
    public ResponseEntity<List<VehicleHealthMetrics>> getVehiclesNeedingMaintenance() {
        return ResponseEntity.ok(maintenanceService.getVehiclesNeedingMaintenance());
    }

    // Dashboard and Analytics Endpoints
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @GetMapping("/dashboard/stats")
    public ResponseEntity<Map<String, Object>> getMaintenanceDashboardStats() {
        List<MaintenanceLog> activeAlerts = maintenanceService.getActiveAlerts();
        List<VehicleHealthMetrics> allMetrics = maintenanceService.getAllVehicleHealthMetrics();
        List<VehicleHealthMetrics> needingMaintenance = maintenanceService.getVehiclesNeedingMaintenance();
        Double averageHealthScore = maintenanceService.getAverageFleetHealthScore();

        // Count alerts by severity
        long criticalAlerts = activeAlerts.stream()
                .filter(alert -> alert.getAlertSeverity() == MaintenanceLog.AlertSeverity.CRITICAL)
                .count();
        long highAlerts = activeAlerts.stream()
                .filter(alert -> alert.getAlertSeverity() == MaintenanceLog.AlertSeverity.HIGH)
                .count();
        long mediumAlerts = activeAlerts.stream()
                .filter(alert -> alert.getAlertSeverity() == MaintenanceLog.AlertSeverity.MEDIUM)
                .count();

        // Count vehicles by health status
        long excellentVehicles = allMetrics.stream()
                .filter(m -> m.getHealthStatus() == VehicleHealthMetrics.HealthStatus.EXCELLENT)
                .count();
        long goodVehicles = allMetrics.stream()
                .filter(m -> m.getHealthStatus() == VehicleHealthMetrics.HealthStatus.GOOD)
                .count();
        long fairVehicles = allMetrics.stream()
                .filter(m -> m.getHealthStatus() == VehicleHealthMetrics.HealthStatus.FAIR)
                .count();
        long poorVehicles = allMetrics.stream()
                .filter(m -> m.getHealthStatus() == VehicleHealthMetrics.HealthStatus.POOR)
                .count();
        long criticalVehicles = allMetrics.stream()
                .filter(m -> m.getHealthStatus() == VehicleHealthMetrics.HealthStatus.CRITICAL)
                .count();

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalActiveAlerts", activeAlerts.size());
        stats.put("criticalAlerts", criticalAlerts);
        stats.put("highAlerts", highAlerts);
        stats.put("mediumAlerts", mediumAlerts);
        stats.put("vehiclesNeedingMaintenance", needingMaintenance.size());
        stats.put("averageFleetHealthScore", averageHealthScore);
        stats.put("totalVehiclesMonitored", allMetrics.size());
        
        // Health status distribution
        Map<String, Long> healthDistribution = new HashMap<>();
        healthDistribution.put("excellent", excellentVehicles);
        healthDistribution.put("good", goodVehicles);
        healthDistribution.put("fair", fairVehicles);
        healthDistribution.put("poor", poorVehicles);
        healthDistribution.put("critical", criticalVehicles);
        stats.put("healthStatusDistribution", healthDistribution);

        return ResponseEntity.ok(stats);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @GetMapping("/dashboard/fleet-health")
    public ResponseEntity<Map<String, Object>> getFleetHealthOverview() {
        Double averageHealthScore = maintenanceService.getAverageFleetHealthScore();
        List<VehicleHealthMetrics> allMetrics = maintenanceService.getAllVehicleHealthMetrics();
        
        Map<String, Object> fleetHealth = new HashMap<>();
        fleetHealth.put("averageHealthScore", averageHealthScore);
        fleetHealth.put("totalVehicles", allMetrics.size());
        
        // Calculate fleet health status
        String fleetStatus;
        if (averageHealthScore >= 85) fleetStatus = "EXCELLENT";
        else if (averageHealthScore >= 70) fleetStatus = "GOOD";
        else if (averageHealthScore >= 55) fleetStatus = "FAIR";
        else if (averageHealthScore >= 40) fleetStatus = "POOR";
        else fleetStatus = "CRITICAL";
        
        fleetHealth.put("fleetHealthStatus", fleetStatus);
        
        // Top 3 vehicles needing attention
        List<VehicleHealthMetrics> needingAttention = maintenanceService.getVehiclesNeedingMaintenance();
        fleetHealth.put("vehiclesNeedingAttention", needingAttention.size());
        
        return ResponseEntity.ok(fleetHealth);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @GetMapping("/predictive-insights")
    public ResponseEntity<Map<String, Object>> getPredictiveInsights() {
        Map<String, Object> insights = new HashMap<>();
        
        // Simulated predictive insights
        insights.put("predictedMaintenanceNeeds", Map.of(
            "nextWeek", 3,
            "nextMonth", 8,
            "nextQuarter", 15
        ));
        
        insights.put("costSavings", Map.of(
            "preventiveMaintenance", "₹2,50,000",
            "reducedDowntime", "₹1,80,000",
            "fuelEfficiencyGains", "₹95,000"
        ));
        
        insights.put("recommendations", List.of(
            "Schedule oil change for FL-002 within 5 days",
            "Replace brake pads for FL-005 before next service",
            "Monitor tire pressure for FL-001 - showing irregular patterns",
            "Battery replacement recommended for FL-003 within 2 weeks"
        ));
        
        return ResponseEntity.ok(insights);
    }
}