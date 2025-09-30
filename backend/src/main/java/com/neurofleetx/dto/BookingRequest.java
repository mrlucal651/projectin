package com.neurofleetx.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class BookingRequest {
    @NotBlank
    private String pickupLocation;

    @NotBlank
    private String deliveryLocation;

    @NotNull
    private Double loadWeight;

    @NotNull
    private LocalDateTime scheduledDate;

    private String notes;

    // Constructors
    public BookingRequest() {}

    public BookingRequest(String pickupLocation, String deliveryLocation, Double loadWeight, LocalDateTime scheduledDate) {
        this.pickupLocation = pickupLocation;
        this.deliveryLocation = deliveryLocation;
        this.loadWeight = loadWeight;
        this.scheduledDate = scheduledDate;
    }

    // Getters and Setters
    public String getPickupLocation() { return pickupLocation; }
    public void setPickupLocation(String pickupLocation) { this.pickupLocation = pickupLocation; }

    public String getDeliveryLocation() { return deliveryLocation; }
    public void setDeliveryLocation(String deliveryLocation) { this.deliveryLocation = deliveryLocation; }

    public Double getLoadWeight() { return loadWeight; }
    public void setLoadWeight(Double loadWeight) { this.loadWeight = loadWeight; }

    public LocalDateTime getScheduledDate() { return scheduledDate; }
    public void setScheduledDate(LocalDateTime scheduledDate) { this.scheduledDate = scheduledDate; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}