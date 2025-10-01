package com.neurofleetx.dto;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public class OptimizationRequest {
    @NotEmpty
    private List<String> bookingIds;
    
    private String optimizationType; // "ROUTE", "LOAD", or "COMBINED"
    private Double maxDistance;
    private Integer maxVehicles;
    private Boolean prioritizeElectric;

    // Constructors
    public OptimizationRequest() {}

    public OptimizationRequest(List<String> bookingIds, String optimizationType) {
        this.bookingIds = bookingIds;
        this.optimizationType = optimizationType;
    }

    // Getters and Setters
    public List<String> getBookingIds() { return bookingIds; }
    public void setBookingIds(List<String> bookingIds) { this.bookingIds = bookingIds; }

    public String getOptimizationType() { return optimizationType; }
    public void setOptimizationType(String optimizationType) { this.optimizationType = optimizationType; }

    public Double getMaxDistance() { return maxDistance; }
    public void setMaxDistance(Double maxDistance) { this.maxDistance = maxDistance; }

    public Integer getMaxVehicles() { return maxVehicles; }
    public void setMaxVehicles(Integer maxVehicles) { this.maxVehicles = maxVehicles; }

    public Boolean getPrioritizeElectric() { return prioritizeElectric; }
    public void setPrioritizeElectric(Boolean prioritizeElectric) { this.prioritizeElectric = prioritizeElectric; }
}