package com.neurofleetx.config;

import com.neurofleetx.model.Route;
import com.neurofleetx.model.Vehicle;
import com.neurofleetx.model.User;
import com.neurofleetx.model.Booking;
import com.neurofleetx.model.Trip;
import com.neurofleetx.repository.RouteRepository;
import com.neurofleetx.repository.VehicleRepository;
import com.neurofleetx.repository.UserRepository;
import com.neurofleetx.repository.BookingRepository;
import com.neurofleetx.repository.TripRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private RouteRepository routeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Initialize sample users if database is empty
        if (userRepository.count() == 0) {
            initializeUsers();
        }

        // Initialize sample vehicles if database is empty
        if (vehicleRepository.count() == 0) {
            initializeVehicles();
        }

        // Initialize sample routes if database is empty
        if (routeRepository.count() == 0) {
            initializeRoutes();
        }

        // Initialize sample bookings and trips
        if (bookingRepository.count() == 0) {
            initializeBookingsAndTrips();
        }
    }

    private void initializeUsers() {
        // Fleet Manager
        User fleetManager = new User("Admin", "Manager", "admin@neurofleetx.com", 
                                   "NeuroFleetX Corp", passwordEncoder.encode("password123"), "fleet_manager");
        fleetManager.setRole(User.Role.ADMIN);
        userRepository.save(fleetManager);

        // Customer
        User customer = new User("John", "Doe", "customer@techsolutions.com", 
                               "Tech Solutions Ltd", passwordEncoder.encode("password123"), "customer");
        userRepository.save(customer);

        // Driver
        User driver = new User("Rajesh", "Kumar", "driver@neurofleetx.com", 
                             "NeuroFleetX Corp", passwordEncoder.encode("password123"), "driver");
        userRepository.save(driver);
    }

    private void initializeVehicles() {
        Vehicle van1 = new Vehicle("FL-001", "Delivery Van", "Tata Ace", 
                                  Vehicle.VehicleStatus.EN_ROUTE, 28.6139, 77.2090);
        van1.setCurrentLocation("Connaught Place");
        van1.setDestination("Khan Market");
        van1.setBatteryLevel(85);
        van1.setSpeed(45.0);
        van1.setDriverName("Rajesh Kumar");
        van1.setCapacity(1500);
        van1.setIsElectric(false);

        Vehicle truck1 = new Vehicle("FL-002", "Cargo Truck", "Ashok Leyland", 
                                    Vehicle.VehicleStatus.LOADING, 28.4595, 77.0266);
        truck1.setCurrentLocation("Gurgaon Warehouse");
        truck1.setDestination("Cyber City");
        truck1.setBatteryLevel(92);
        truck1.setSpeed(0.0);
        truck1.setDriverName("Priya Sharma");
        truck1.setCapacity(3000);
        truck1.setIsElectric(false);

        Vehicle van2 = new Vehicle("FL-003", "Pickup Truck", "Mahindra Bolero", 
                                  Vehicle.VehicleStatus.AVAILABLE, 28.5355, 77.3910);
        van2.setCurrentLocation("Noida Base Station");
        van2.setDestination("Greater Noida");
        van2.setBatteryLevel(100);
        van2.setSpeed(0.0);
        van2.setDriverName("Amit Singh");
        van2.setCapacity(1000);
        van2.setIsElectric(false);

        Vehicle electric1 = new Vehicle("FL-004", "Electric Van", "Tata Nexon EV", 
                                       Vehicle.VehicleStatus.EN_ROUTE, 12.9716, 77.5946);
        electric1.setCurrentLocation("Bangalore Electronic City");
        electric1.setDestination("Whitefield Tech Park");
        electric1.setBatteryLevel(67);
        electric1.setSpeed(32.0);
        electric1.setDriverName("Sneha Patel");
        electric1.setCapacity(1200);
        electric1.setIsElectric(true);

        // Add more vehicles for better maintenance status distribution
        Vehicle maintenance1 = new Vehicle("FL-005", "Cargo Truck", "Tata LPT", 
                                         Vehicle.VehicleStatus.MAINTENANCE, 28.7041, 77.1025);
        maintenance1.setCurrentLocation("Delhi Service Center");
        maintenance1.setBatteryLevel(0);
        maintenance1.setSpeed(0.0);
        maintenance1.setDriverName("Vikram Reddy");
        maintenance1.setCapacity(5000);
        maintenance1.setIsElectric(false);

        Vehicle lowBattery1 = new Vehicle("FL-006", "Delivery Van", "Mahindra Jeeto", 
                                        Vehicle.VehicleStatus.AVAILABLE, 28.4089, 77.3178);
        lowBattery1.setCurrentLocation("Faridabad Depot");
        lowBattery1.setBatteryLevel(25);
        lowBattery1.setSpeed(0.0);
        lowBattery1.setDriverName("Ravi Gupta");
        lowBattery1.setCapacity(750);
        lowBattery1.setIsElectric(false);

        Vehicle offline1 = new Vehicle("FL-007", "Pickup Truck", "Isuzu D-Max", 
                                     Vehicle.VehicleStatus.OFFLINE, 28.6692, 77.4538);
        offline1.setCurrentLocation("Ghaziabad Hub");
        offline1.setBatteryLevel(45);
        offline1.setSpeed(0.0);
        offline1.setDriverName("Suresh Kumar");
        offline1.setCapacity(1500);
        offline1.setIsElectric(false);
        vehicleRepository.save(van1);
        vehicleRepository.save(truck1);
        vehicleRepository.save(van2);
        vehicleRepository.save(electric1);
        vehicleRepository.save(maintenance1);
        vehicleRepository.save(lowBattery1);
        vehicleRepository.save(offline1);
    }

    private void initializeRoutes() {
        Route route1 = new Route("RT-001", "Delhi Central Warehouse", "Connaught Place Hub", 18.5, 35, 8.5);
        route1.setStatus(Route.RouteStatus.ACTIVE);
        route1.setAiOptimizationSavings(23.0);
        route1.setTimeSaved(15);
        route1.setFuelSaved(3.2);

        Route route2 = new Route("RT-002", "Gurgaon Distribution Center", "DLF Mall", 25.2, 42, 12.8);
        route2.setStatus(Route.RouteStatus.PLANNED);
        route2.setAiOptimizationSavings(18.0);
        route2.setTimeSaved(12);
        route2.setFuelSaved(2.8);

        Route route3 = new Route("RT-003", "Noida Terminal", "Greater Noida Industrial Zone", 32.8, 58, 15.9);
        route3.setStatus(Route.RouteStatus.COMPLETED);
        route3.setAiOptimizationSavings(31.0);
        route3.setTimeSaved(22);
        route3.setFuelSaved(4.1);

        routeRepository.save(route1);
        routeRepository.save(route2);
        routeRepository.save(route3);
    }

    private void initializeBookingsAndTrips() {
        User customer = userRepository.findByEmail("customer@techsolutions.com").orElse(null);
        User driver = userRepository.findByEmail("driver@neurofleetx.com").orElse(null);
        Vehicle vehicle = vehicleRepository.findByVehicleId("FL-001").orElse(null);

        if (customer != null && driver != null && vehicle != null) {
            // Sample Booking
            Booking booking1 = new Booking("BK-001", customer, "Delhi Central Warehouse", 
                                         "Connaught Place Hub", 18.5, 1500.0);
            booking1.setDriver(driver);
            booking1.setVehicle(vehicle);
            booking1.setStatus(Booking.BookingStatus.IN_TRANSIT);
            booking1.setProgress(65);
            booking1.setPickupTime(LocalDateTime.now().minusHours(1));
            booking1.setEstimatedDelivery(LocalDateTime.now().plusMinutes(25));
            bookingRepository.save(booking1);

            // Sample Trip
            Trip trip1 = new Trip("TR-001", driver, vehicle, "Delhi Central Warehouse", 
                                "Connaught Place Hub", 18.5, 850.0);
            trip1.setBooking(booking1);
            trip1.setStatus(Trip.TripStatus.IN_PROGRESS);
            trip1.setProgress(65);
            trip1.setStartTime(LocalDateTime.now().minusHours(1));
            trip1.setEstimatedCompletion(LocalDateTime.now().plusMinutes(25));
            trip1.setCustomerName("John Doe");
            trip1.setCustomerPhone("+91 98765 43210");
            tripRepository.save(trip1);
        }
    }
}