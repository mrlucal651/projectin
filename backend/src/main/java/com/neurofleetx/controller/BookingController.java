package com.neurofleetx.controller;

import com.neurofleetx.dto.BookingRequest;
import com.neurofleetx.dto.VehicleRecommendation;
import com.neurofleetx.model.Booking;
import com.neurofleetx.model.User;
import com.neurofleetx.service.BookingService;
import com.neurofleetx.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/bookings")
public class BookingController {
    
    @Autowired
    private BookingService bookingService;
    
    @Autowired
    private UserService userService;

    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @GetMapping
    public ResponseEntity<List<Booking>> getAllBookings() {
        return ResponseEntity.ok(bookingService.getAllBookings());
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @GetMapping("/active")
    public ResponseEntity<List<Booking>> getActiveBookings() {
        return ResponseEntity.ok(bookingService.getActiveBookings());
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("/my-bookings")
    public ResponseEntity<List<Booking>> getMyBookings(Authentication authentication) {
        User customer = userService.findByEmail(authentication.getName()).orElseThrow();
        return ResponseEntity.ok(bookingService.getBookingsByCustomer(customer));
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('CUSTOMER') or hasRole('DRIVER')")
    @GetMapping("/{bookingId}")
    public ResponseEntity<Booking> getBookingDetails(@PathVariable String bookingId) {
        return bookingService.getBookingByBookingId(bookingId)
                .map(booking -> ResponseEntity.ok().body(booking))
                .orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping
    public ResponseEntity<Booking> createBooking(@Valid @RequestBody BookingRequest bookingRequest, 
                                                Authentication authentication) {
        User customer = userService.findByEmail(authentication.getName()).orElseThrow();
        Booking savedBooking = bookingService.createBooking(customer, bookingRequest);
        return ResponseEntity.ok(savedBooking);
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping("/recommendations")
    public ResponseEntity<List<VehicleRecommendation>> getSmartRecommendations(
            @Valid @RequestBody BookingRequest bookingRequest) {
        List<VehicleRecommendation> recommendations = bookingService.getSmartRecommendations(bookingRequest);
        return ResponseEntity.ok(recommendations);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('DRIVER')")
    @PutMapping("/{bookingId}/progress")
    public ResponseEntity<Booking> updateBookingProgress(
            @PathVariable String bookingId,
            @RequestBody Map<String, Integer> progressData) {
        try {
            Integer progress = progressData.get("progress");
            Booking updatedBooking = bookingService.updateBookingProgress(bookingId, progress);
            return ResponseEntity.ok(updatedBooking);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping("/{bookingId}/rate")
    public ResponseEntity<Booking> rateBooking(
            @PathVariable String bookingId,
            @RequestBody Map<String, Integer> ratingData) {
        return bookingService.getBookingByBookingId(bookingId)
                .map(booking -> {
                    booking.setRating(ratingData.get("rating"));
                    return ResponseEntity.ok(bookingService.updateBooking(booking));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @PutMapping("/{bookingId}/cancel")
    public ResponseEntity<Booking> cancelBooking(@PathVariable String bookingId) {
        return bookingService.getBookingByBookingId(bookingId)
                .map(booking -> {
                    booking.setStatus(Booking.BookingStatus.CANCELLED);
                    return ResponseEntity.ok(bookingService.updateBooking(booking));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBooking(@PathVariable Long id) {
        return bookingService.getBookingById(id)
                .map(booking -> {
                    bookingService.deleteBooking(id);
                    return ResponseEntity.ok().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}