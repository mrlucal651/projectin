package com.neurofleetx.controller;

import com.neurofleetx.model.Vehicle;
import com.neurofleetx.service.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/vehicles")
public class VehicleController {
    
    @Autowired
    private VehicleService vehicleService;

    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @GetMapping
    public ResponseEntity<List<Vehicle>> getAllVehicles() {
        return ResponseEntity.ok(vehicleService.getAllVehicles());
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @GetMapping("/active")
    public ResponseEntity<List<Vehicle>> getActiveVehicles() {
        return ResponseEntity.ok(vehicleService.getActiveVehicles());
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @GetMapping("/{id}")
    public ResponseEntity<Vehicle> getVehicleById(@PathVariable Long id) {
        return vehicleService.getVehicleById(id)
                .map(vehicle -> ResponseEntity.ok().body(vehicle))
                .orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @GetMapping("/vehicle/{vehicleId}")
    public ResponseEntity<Vehicle> getVehicleByVehicleId(@PathVariable String vehicleId) {
        return vehicleService.getVehicleByVehicleId(vehicleId)
                .map(vehicle -> ResponseEntity.ok().body(vehicle))
                .orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @PostMapping
    public ResponseEntity<Vehicle> createVehicle(@RequestBody Vehicle vehicle) {
        Vehicle savedVehicle = vehicleService.createVehicle(vehicle);
        return ResponseEntity.ok(savedVehicle);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @PutMapping("/{id}")
    public ResponseEntity<Vehicle> updateVehicle(@PathVariable Long id, @RequestBody Vehicle vehicleDetails) {
        return vehicleService.getVehicleById(id)
                .map(vehicle -> {
                    vehicle.setType(vehicleDetails.getType());
                    vehicle.setModel(vehicleDetails.getModel());
                    vehicle.setStatus(vehicleDetails.getStatus());
                    vehicle.setLatitude(vehicleDetails.getLatitude());
                    vehicle.setLongitude(vehicleDetails.getLongitude());
                    vehicle.setCurrentLocation(vehicleDetails.getCurrentLocation());
                    vehicle.setDestination(vehicleDetails.getDestination());
                    vehicle.setBatteryLevel(vehicleDetails.getBatteryLevel());
                    vehicle.setSpeed(vehicleDetails.getSpeed());
                    vehicle.setDriverName(vehicleDetails.getDriverName());
                    return ResponseEntity.ok(vehicleService.updateVehicle(vehicle));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('DRIVER')")
    @PutMapping("/{vehicleId}/location")
    public ResponseEntity<Vehicle> updateVehicleLocation(
            @PathVariable String vehicleId,
            @RequestBody Map<String, Object> locationData) {
        try {
            Double latitude = Double.valueOf(locationData.get("latitude").toString());
            Double longitude = Double.valueOf(locationData.get("longitude").toString());
            String location = locationData.get("location").toString();
            
            Vehicle updatedVehicle = vehicleService.updateVehicleLocation(vehicleId, latitude, longitude, location);
            return ResponseEntity.ok(updatedVehicle);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getVehicleStats() {
        Map<String, Object> stats = Map.of(
            "total", vehicleService.getAllVehicles().size(),
            "active", vehicleService.getVehicleCountByStatus(Vehicle.VehicleStatus.EN_ROUTE),
            "available", vehicleService.getVehicleCountByStatus(Vehicle.VehicleStatus.AVAILABLE),
            "maintenance", vehicleService.getVehicleCountByStatus(Vehicle.VehicleStatus.MAINTENANCE)
        );
        return ResponseEntity.ok(stats);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @GetMapping("/maintenance-status")
    public ResponseEntity<Map<String, Integer>> getMaintenanceStatus() {
        List<Vehicle> allVehicles = vehicleService.getAllVehicles();
        
        Map<String, Integer> maintenanceStatus = new HashMap<>();
        int healthy = 0;
        int due = 0;
        int critical = 0;
        
        for (Vehicle vehicle : allVehicles) {
            // Simulate maintenance status based on vehicle status and battery level
            if (vehicle.getStatus() == Vehicle.VehicleStatus.MAINTENANCE) {
                critical++;
            } else if (vehicle.getBatteryLevel() != null && vehicle.getBatteryLevel() < 30) {
                due++;
            } else if (vehicle.getStatus() == Vehicle.VehicleStatus.OFFLINE) {
                due++;
            } else {
                healthy++;
            }
        }
        
        maintenanceStatus.put("healthy", healthy);
        maintenanceStatus.put("due", due);
        maintenanceStatus.put("critical", critical);
        
        return ResponseEntity.ok(maintenanceStatus);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteVehicle(@PathVariable Long id) {
        return vehicleService.getVehicleById(id)
                .map(vehicle -> {
                    vehicleService.deleteVehicle(id);
                    return ResponseEntity.ok().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}