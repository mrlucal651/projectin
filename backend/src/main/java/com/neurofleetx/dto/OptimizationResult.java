package com.neurofleetx.dto;

import com.neurofleetx.model.Vehicle;
import com.neurofleetx.model.Booking;
import java.util.List;
import java.util.Map;

public class OptimizationResult {
    private List<VehicleAssignment> assignments;
    private OptimizationMetrics metrics;
    private List<String> optimizedRoute;
    private String optimizationType;

    // Constructors
    public OptimizationResult() {}

    public OptimizationResult(List<VehicleAssignment> assignments, OptimizationMetrics metrics, String optimizationType) {
        this.assignments = assignments;
        this.metrics = metrics;
        this.optimizationType = optimizationType;
    }

    // Getters and Setters
    public List<VehicleAssignment> getAssignments() { return assignments; }
    public void setAssignments(List<VehicleAssignment> assignments) { this.assignments = assignments; }

    public OptimizationMetrics getMetrics() { return metrics; }
    public void setMetrics(OptimizationMetrics metrics) { this.metrics = metrics; }

    public List<String> getOptimizedRoute() { return optimizedRoute; }
    public void setOptimizedRoute(List<String> optimizedRoute) { this.optimizedRoute = optimizedRoute; }

    public String getOptimizationType() { return optimizationType; }
    public void setOptimizationType(String optimizationType) { this.optimizationType = optimizationType; }

    // Inner classes
    public static class VehicleAssignment {
        private Vehicle vehicle;
        private List<Booking> assignedBookings;
        private Double totalDistance;
        private Double totalLoad;
        private Double utilizationRate;
        private Integer estimatedTime;
        private List<String> optimizedRoute;

        // Constructors
        public VehicleAssignment() {}

        public VehicleAssignment(Vehicle vehicle, List<Booking> assignedBookings) {
            this.vehicle = vehicle;
            this.assignedBookings = assignedBookings;
        }

        // Getters and Setters
        public Vehicle getVehicle() { return vehicle; }
        public void setVehicle(Vehicle vehicle) { this.vehicle = vehicle; }

        public List<Booking> getAssignedBookings() { return assignedBookings; }
        public void setAssignedBookings(List<Booking> assignedBookings) { this.assignedBookings = assignedBookings; }

        public Double getTotalDistance() { return totalDistance; }
        public void setTotalDistance(Double totalDistance) { this.totalDistance = totalDistance; }

        public Double getTotalLoad() { return totalLoad; }
        public void setTotalLoad(Double totalLoad) { this.totalLoad = totalLoad; }

        public Double getUtilizationRate() { return utilizationRate; }
        public void setUtilizationRate(Double utilizationRate) { this.utilizationRate = utilizationRate; }

        public Integer getEstimatedTime() { return estimatedTime; }
        public void setEstimatedTime(Integer estimatedTime) { this.estimatedTime = estimatedTime; }

        public List<String> getOptimizedRoute() { return optimizedRoute; }
        public void setOptimizedRoute(List<String> optimizedRoute) { this.optimizedRoute = optimizedRoute; }
    }

    public static class OptimizationMetrics {
        private Double totalDistance;
        private Double fuelSavings;
        private Integer timeSavings;
        private Double costReduction;
        private Double averageUtilization;
        private Integer vehiclesUsed;
        private String algorithm;

        // Constructors
        public OptimizationMetrics() {}

        // Getters and Setters
        public Double getTotalDistance() { return totalDistance; }
        public void setTotalDistance(Double totalDistance) { this.totalDistance = totalDistance; }

        public Double getFuelSavings() { return fuelSavings; }
        public void setFuelSavings(Double fuelSavings) { this.fuelSavings = fuelSavings; }

        public Integer getTimeSavings() { return timeSavings; }
        public void setTimeSavings(Integer timeSavings) { this.timeSavings = timeSavings; }

        public Double getCostReduction() { return costReduction; }
        public void setCostReduction(Double costReduction) { this.costReduction = costReduction; }

        public Double getAverageUtilization() { return averageUtilization; }
        public void setAverageUtilization(Double averageUtilization) { this.averageUtilization = averageUtilization; }

        public Integer getVehiclesUsed() { return vehiclesUsed; }
        public void setVehiclesUsed(Integer vehiclesUsed) { this.vehiclesUsed = vehiclesUsed; }

        public String getAlgorithm() { return algorithm; }
        public void setAlgorithm(String algorithm) { this.algorithm = algorithm; }
    }
}