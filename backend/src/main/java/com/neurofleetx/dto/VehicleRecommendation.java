package com.neurofleetx.dto;

import com.neurofleetx.model.Vehicle;

public class VehicleRecommendation {
    private Vehicle vehicle;
    private Double matchScore;
    private String reason;
    private Double estimatedCost;
    private Integer estimatedTime; // in minutes

    // Constructors
    public VehicleRecommendation() {}

    public VehicleRecommendation(Vehicle vehicle, Double matchScore, String reason, Double estimatedCost, Integer estimatedTime) {
        this.vehicle = vehicle;
        this.matchScore = matchScore;
        this.reason = reason;
        this.estimatedCost = estimatedCost;
        this.estimatedTime = estimatedTime;
    }

    // Getters and Setters
    public Vehicle getVehicle() { return vehicle; }
    public void setVehicle(Vehicle vehicle) { this.vehicle = vehicle; }

    public Double getMatchScore() { return matchScore; }
    public void setMatchScore(Double matchScore) { this.matchScore = matchScore; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public Double getEstimatedCost() { return estimatedCost; }
    public void setEstimatedCost(Double estimatedCost) { this.estimatedCost = estimatedCost; }

    public Integer getEstimatedTime() { return estimatedTime; }
    public void setEstimatedTime(Integer estimatedTime) { this.estimatedTime = estimatedTime; }
}