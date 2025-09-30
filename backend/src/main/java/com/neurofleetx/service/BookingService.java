package com.neurofleetx.service;

import com.neurofleetx.dto.BookingRequest;
import com.neurofleetx.dto.VehicleRecommendation;
import com.neurofleetx.model.Booking;
import com.neurofleetx.model.User;
import com.neurofleetx.model.Vehicle;
import com.neurofleetx.repository.BookingRepository;
import com.neurofleetx.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.Comparator;

@Service
public class BookingService {
    
    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    private final Random random = new Random();

    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    public List<Booking> getBookingsByCustomer(User customer) {
        return bookingRepository.findByCustomerIdOrderByCreatedAtDesc(customer.getId());
    }

    public List<Booking> getBookingsByDriver(User driver) {
        return bookingRepository.findByDriverIdOrderByCreatedAtDesc(driver.getId());
    }

    public List<Booking> getActiveBookings() {
        return bookingRepository.findActiveBookings();
    }

    public Optional<Booking> getBookingById(Long id) {
        return bookingRepository.findById(id);
    }

    public Optional<Booking> getBookingByBookingId(String bookingId) {
        return bookingRepository.findByBookingId(bookingId);
    }

    public Booking createBooking(User customer, BookingRequest bookingRequest) {
        // Generate booking ID
        String bookingId = "BK-" + String.format("%03d", random.nextInt(1000));
        
        // Calculate distance (simplified - in real app would use maps API)
        Double distance = calculateDistance(bookingRequest.getPickupLocation(), bookingRequest.getDeliveryLocation());
        
        // Calculate estimated cost
        Double estimatedCost = calculateEstimatedCost(distance, bookingRequest.getLoadWeight());
        
        Booking booking = new Booking(bookingId, customer, bookingRequest.getPickupLocation(), 
                                    bookingRequest.getDeliveryLocation(), distance, estimatedCost, 
                                    bookingRequest.getLoadWeight(), bookingRequest.getScheduledDate());
        
        booking.setNotes(bookingRequest.getNotes());
        
        // Set estimated delivery time (1-3 hours from scheduled date)
        booking.setEstimatedDelivery(bookingRequest.getScheduledDate().plusHours(1 + random.nextInt(3)));
        
        return bookingRepository.save(booking);
    }

    public Booking updateBooking(Booking booking) {
        return bookingRepository.save(booking);
    }

    public Booking updateBookingProgress(String bookingId, Integer progress) {
        Optional<Booking> bookingOpt = bookingRepository.findByBookingId(bookingId);
        if (bookingOpt.isPresent()) {
            Booking booking = bookingOpt.get();
            booking.setProgress(progress);
            
            // Update status based on progress
            if (progress >= 100) {
                booking.setStatus(Booking.BookingStatus.DELIVERED);
                booking.setDeliveryTime(LocalDateTime.now());
            } else if (progress > 0) {
                booking.setStatus(Booking.BookingStatus.IN_TRANSIT);
                if (booking.getPickupTime() == null) {
                    booking.setPickupTime(LocalDateTime.now());
                }
            }
            
            return bookingRepository.save(booking);
        }
        throw new RuntimeException("Booking not found: " + bookingId);
    }

    public List<VehicleRecommendation> getSmartRecommendations(BookingRequest bookingRequest) {
        List<Vehicle> availableVehicles = vehicleRepository.findByStatus(Vehicle.VehicleStatus.AVAILABLE);
        
        return availableVehicles.stream()
                .map(vehicle -> calculateRecommendationScore(vehicle, bookingRequest))
                .filter(rec -> rec.getMatchScore() > 0.3) // Only show vehicles with decent match
                .sorted(Comparator.comparing(VehicleRecommendation::getMatchScore).reversed())
                .limit(5) // Top 5 recommendations
                .collect(Collectors.toList());
    }

    private VehicleRecommendation calculateRecommendationScore(Vehicle vehicle, BookingRequest bookingRequest) {
        double score = 0.0;
        StringBuilder reason = new StringBuilder();
        
        // Capacity check (40% of score)
        Double vehicleCapacity = vehicle.getMaxLoadCapacity() != null ? vehicle.getMaxLoadCapacity() : 
                               (vehicle.getCapacity() != null ? vehicle.getCapacity().doubleValue() : 1000.0);
        
        if (vehicleCapacity >= bookingRequest.getLoadWeight()) {
            double capacityRatio = bookingRequest.getLoadWeight() / vehicleCapacity;
            if (capacityRatio <= 0.8) {
                score += 0.4 * (1.0 - capacityRatio); // Better score for not overloading
                reason.append("Optimal capacity utilization. ");
            } else {
                score += 0.2; // Still acceptable but near capacity
                reason.append("Near capacity limit. ");
            }
        } else {
            return new VehicleRecommendation(vehicle, 0.0, "Insufficient capacity", 0.0, 0);
        }
        
        // Vehicle type suitability (25% of score)
        String vehicleType = vehicle.getType().toLowerCase();
        if (bookingRequest.getLoadWeight() > 2000 && vehicleType.contains("truck")) {
            score += 0.25;
            reason.append("Heavy-duty vehicle for large load. ");
        } else if (bookingRequest.getLoadWeight() <= 1000 && vehicleType.contains("van")) {
            score += 0.25;
            reason.append("Efficient vehicle for light load. ");
        } else {
            score += 0.15;
            reason.append("Suitable vehicle type. ");
        }
        
        // Battery/fuel level (20% of score)
        if (vehicle.getBatteryLevel() != null) {
            if (vehicle.getBatteryLevel() > 80) {
                score += 0.2;
                reason.append("Excellent battery level. ");
            } else if (vehicle.getBatteryLevel() > 50) {
                score += 0.15;
                reason.append("Good battery level. ");
            } else {
                score += 0.05;
                reason.append("Low battery level. ");
            }
        }
        
        // Electric vehicle bonus for eco-friendly (10% of score)
        if (vehicle.getIsElectric() != null && vehicle.getIsElectric()) {
            score += 0.1;
            reason.append("Eco-friendly electric vehicle. ");
        }
        
        // Distance from pickup (5% of score) - simplified calculation
        double distanceFromPickup = calculateDistanceFromPickup(vehicle, bookingRequest.getPickupLocation());
        if (distanceFromPickup < 10) {
            score += 0.05;
            reason.append("Close to pickup location. ");
        }
        
        // Calculate estimated cost and time
        Double distance = calculateDistance(bookingRequest.getPickupLocation(), bookingRequest.getDeliveryLocation());
        Double estimatedCost = calculateEstimatedCost(distance, bookingRequest.getLoadWeight());
        Integer estimatedTime = (int) (distance * 2.5); // Rough estimate: 2.5 minutes per km
        
        return new VehicleRecommendation(vehicle, Math.min(score, 1.0), reason.toString().trim(), 
                                       estimatedCost, estimatedTime);
    }

    private Double calculateDistance(String pickup, String delivery) {
        // Simplified distance calculation - in real app would use Google Maps API
        // For demo, return random distance between 5-50 km
        return 5.0 + (random.nextDouble() * 45.0);
    }

    private Double calculateDistanceFromPickup(Vehicle vehicle, String pickupLocation) {
        // Simplified - in real app would calculate actual distance
        return random.nextDouble() * 20.0; // 0-20 km
    }

    private Double calculateEstimatedCost(Double distance, Double loadWeight) {
        // Base rate: ₹15 per km + ₹2 per kg
        double baseCost = (distance * 15.0) + (loadWeight * 2.0);
        
        // Add service fee (10%)
        double serviceFee = baseCost * 0.1;
        
        return baseCost + serviceFee;
    }

    public void deleteBooking(Long id) {
        bookingRepository.deleteById(id);
    }
}