package com.neurofleetx.service;

import com.neurofleetx.model.Vehicle;
import com.neurofleetx.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class VehicleService {
    
    @Autowired
    private VehicleRepository vehicleRepository;

    public List<Vehicle> getAllVehicles() {
        return vehicleRepository.findAll();
    }

    public List<Vehicle> getActiveVehicles() {
        return vehicleRepository.findActiveVehicles();
    }

    public Optional<Vehicle> getVehicleById(Long id) {
        return vehicleRepository.findById(id);
    }

    public Optional<Vehicle> getVehicleByVehicleId(String vehicleId) {
        return vehicleRepository.findByVehicleId(vehicleId);
    }

    public Vehicle createVehicle(Vehicle vehicle) {
        return vehicleRepository.save(vehicle);
    }

    public Vehicle updateVehicle(Vehicle vehicle) {
        vehicle.setLastUpdated(LocalDateTime.now());
        return vehicleRepository.save(vehicle);
    }

    public Vehicle updateVehicleLocation(String vehicleId, Double latitude, Double longitude, String location) {
        Optional<Vehicle> vehicleOpt = vehicleRepository.findByVehicleId(vehicleId);
        if (vehicleOpt.isPresent()) {
            Vehicle vehicle = vehicleOpt.get();
            vehicle.setLatitude(latitude);
            vehicle.setLongitude(longitude);
            vehicle.setCurrentLocation(location);
            vehicle.setLastUpdated(LocalDateTime.now());
            return vehicleRepository.save(vehicle);
        }
        throw new RuntimeException("Vehicle not found: " + vehicleId);
    }

    public Long getVehicleCountByStatus(Vehicle.VehicleStatus status) {
        return vehicleRepository.countByStatus(status);
    }

    public void deleteVehicle(Long id) {
        vehicleRepository.deleteById(id);
    }
}