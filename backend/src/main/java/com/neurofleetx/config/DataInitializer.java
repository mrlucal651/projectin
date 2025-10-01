package com.neurofleetx.config;

import com.neurofleetx.model.Route;
import com.neurofleetx.model.Vehicle;
import com.neurofleetx.model.User;
import com.neurofleetx.model.Role;
import com.neurofleetx.model.Booking;
import com.neurofleetx.model.Trip;
import com.neurofleetx.repository.RouteRepository;
import com.neurofleetx.repository.VehicleRepository;
import com.neurofleetx.repository.UserRepository;
import com.neurofleetx.repository.RoleRepository;
import com.neurofleetx.repository.BookingRepository;
import com.neurofleetx.repository.TripRepository;
import com.neurofleetx.repository.MaintenanceLogRepository;
import com.neurofleetx.repository.VehicleHealthMetricsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private RouteRepository routeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private MaintenanceLogRepository maintenanceLogRepository;

    @Autowired
    private VehicleHealthMetricsRepository healthMetricsRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Initialize roles first
        if (roleRepository.count() == 0) {
            initializeRoles();
        }

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
            initializeBookings();
        }
        
        // Initialize sample trips
        if (tripRepository.count() == 0) {
            initializeTrips();
        }
        
        // Initialize sample optimization data
        initializeOptimizationData();
        
        // Initialize maintenance and health data
        if (maintenanceLogRepository.count() == 0) {
            initializeMaintenanceData();
        }
    }

    private void initializeRoles() {
        roleRepository.save(new Role(Role.ERole.ROLE_ADMIN));
        roleRepository.save(new Role(Role.ERole.ROLE_MANAGER));
        roleRepository.save(new Role(Role.ERole.ROLE_DRIVER));
        roleRepository.save(new Role(Role.ERole.ROLE_CUSTOMER));
    }

    private void initializeUsers() {
        Role adminRole = roleRepository.findByName(Role.ERole.ROLE_ADMIN).orElseThrow();
        Role managerRole = roleRepository.findByName(Role.ERole.ROLE_MANAGER).orElseThrow();
        Role driverRole = roleRepository.findByName(Role.ERole.ROLE_DRIVER).orElseThrow();
        Role customerRole = roleRepository.findByName(Role.ERole.ROLE_CUSTOMER).orElseThrow();

        // Fleet Manager
        User admin = new User("Super", "Admin", "admin@neurofleetx.com", 
                             "NeuroFleetX Corp", passwordEncoder.encode("password123"), "admin");
        admin.setRoles(Set.of(adminRole));
        userRepository.save(admin);

        // Manager
        User manager = new User("Fleet", "Manager", "manager@neurofleetx.com", 
                                   "NeuroFleetX Corp", passwordEncoder.encode("password123"), "fleet_manager");
        manager.setRoles(Set.of(managerRole));
        userRepository.save(manager);

        // Customer
        User customer = new User("John", "Doe", "customer@techsolutions.com", 
                               "Tech Solutions Ltd", passwordEncoder.encode("password123"), "customer");
        customer.setRoles(Set.of(customerRole));
        userRepository.save(customer);

        // Driver
        User driver = new User("Rajesh", "Kumar", "driver@neurofleetx.com", 
                             "NeuroFleetX Corp", passwordEncoder.encode("password123"), "driver");
        driver.setRoles(Set.of(driverRole));
        userRepository.save(driver);

        // Additional test users
        User customer2 = new User("Jane", "Smith", "jane@logistics.com", 
                                "Logistics Inc", passwordEncoder.encode("password123"), "customer");
        customer2.setRoles(Set.of(customerRole));
        userRepository.save(customer2);

        User driver2 = new User("Priya", "Sharma", "priya@neurofleetx.com", 
                              "NeuroFleetX Corp", passwordEncoder.encode("password123"), "driver");
        driver2.setRoles(Set.of(driverRole));
        userRepository.save(driver2);
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
        van1.setMaxLoadCapacity(1200.0);
        van1.setIsElectric(false);

        Vehicle truck1 = new Vehicle("FL-002", "Cargo Truck", "Ashok Leyland", 
                                    Vehicle.VehicleStatus.LOADING, 28.4595, 77.0266);
        truck1.setCurrentLocation("Gurgaon Warehouse");
        truck1.setDestination("Cyber City");
        truck1.setBatteryLevel(92);
        truck1.setSpeed(0.0);
        truck1.setDriverName("Priya Sharma");
        truck1.setCapacity(3000);
        truck1.setMaxLoadCapacity(2500.0);
        truck1.setIsElectric(false);

        Vehicle van2 = new Vehicle("FL-003", "Pickup Truck", "Mahindra Bolero", 
                                  Vehicle.VehicleStatus.AVAILABLE, 28.5355, 77.3910);
        van2.setCurrentLocation("Noida Base Station");
        van2.setDestination("Greater Noida");
        van2.setBatteryLevel(100);
        van2.setSpeed(0.0);
        van2.setDriverName("Amit Singh");
        van2.setCapacity(1000);
        van2.setMaxLoadCapacity(800.0);
        van2.setIsElectric(false);

        Vehicle electric1 = new Vehicle("FL-004", "Electric Van", "Tata Nexon EV", 
                                       Vehicle.VehicleStatus.EN_ROUTE, 12.9716, 77.5946);
        electric1.setCurrentLocation("Bangalore Electronic City");
        electric1.setDestination("Whitefield Tech Park");
        electric1.setBatteryLevel(67);
        electric1.setSpeed(32.0);
        electric1.setDriverName("Sneha Patel");
        electric1.setCapacity(1200);
        electric1.setMaxLoadCapacity(1000.0);
        electric1.setIsElectric(true);

        // Add more vehicles for better maintenance status distribution
        Vehicle maintenance1 = new Vehicle("FL-005", "Cargo Truck", "Tata LPT", 
                                         Vehicle.VehicleStatus.MAINTENANCE, 28.7041, 77.1025);
        maintenance1.setCurrentLocation("Delhi Service Center");
        maintenance1.setBatteryLevel(0);
        maintenance1.setSpeed(0.0);
        maintenance1.setDriverName("Vikram Reddy");
        maintenance1.setCapacity(5000);
        maintenance1.setMaxLoadCapacity(4000.0);
        maintenance1.setIsElectric(false);

        Vehicle lowBattery1 = new Vehicle("FL-006", "Delivery Van", "Mahindra Jeeto", 
                                        Vehicle.VehicleStatus.AVAILABLE, 28.4089, 77.3178);
        lowBattery1.setCurrentLocation("Faridabad Depot");
        lowBattery1.setBatteryLevel(25);
        lowBattery1.setSpeed(0.0);
        lowBattery1.setDriverName("Ravi Gupta");
        lowBattery1.setCapacity(750);
        lowBattery1.setMaxLoadCapacity(600.0);
        lowBattery1.setIsElectric(false);

        Vehicle offline1 = new Vehicle("FL-007", "Pickup Truck", "Isuzu D-Max", 
                                     Vehicle.VehicleStatus.OFFLINE, 28.6692, 77.4538);
        offline1.setCurrentLocation("Ghaziabad Hub");
        offline1.setBatteryLevel(45);
        offline1.setSpeed(0.0);
        offline1.setDriverName("Suresh Kumar");
        offline1.setCapacity(1500);
        offline1.setMaxLoadCapacity(1200.0);
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

    private void initializeBookings() {
        User customer = userRepository.findByEmail("customer@techsolutions.com").orElse(null);
        User customer2 = userRepository.findByEmail("jane@logistics.com").orElse(null);
        User driver = userRepository.findByEmail("driver@neurofleetx.com").orElse(null);
        Vehicle vehicle = vehicleRepository.findByVehicleId("FL-001").orElse(null);
        Vehicle vehicle2 = vehicleRepository.findByVehicleId("FL-002").orElse(null);

        if (customer != null && driver != null && vehicle != null) {
            // Sample Booking
            Booking booking1 = new Booking("BK-001", customer, "Delhi Central Warehouse", 
                                         "Connaught Place Hub", 18.5, 1500.0, 500.0, LocalDateTime.now().plusHours(2));
            booking1.setDriver(driver);
            booking1.setVehicle(vehicle);
            booking1.setStatus(Booking.BookingStatus.IN_TRANSIT);
            booking1.setProgress(65);
            booking1.setPickupTime(LocalDateTime.now().minusHours(1));
            booking1.setEstimatedDelivery(LocalDateTime.now().plusMinutes(25));
            booking1.setNotes("Fragile items - handle with care");
            bookingRepository.save(booking1);
        }
        
        if (customer2 != null && vehicle2 != null) {
            // Sample Booking 2
            Booking booking2 = new Booking("BK-002", customer2, "Gurgaon Distribution Center", 
                                         "DLF Mall", 25.2, 2200.0, 1200.0, LocalDateTime.now().plusDays(1));
            booking2.setStatus(Booking.BookingStatus.SCHEDULED);
            booking2.setEstimatedDelivery(LocalDateTime.now().plusDays(1).plusHours(2));
            booking2.setNotes("Bulk delivery - multiple packages");
            bookingRepository.save(booking2);
            
            // Sample Booking 3 - Completed
            Booking booking3 = new Booking("BK-003", customer, "Noida Depot", 
                                         "Greater Noida Tech Park", 32.8, 2800.0, 800.0, LocalDateTime.now().minusDays(1));
            booking3.setStatus(Booking.BookingStatus.DELIVERED);
            booking3.setProgress(100);
            booking3.setPickupTime(LocalDateTime.now().minusDays(1));
            booking3.setDeliveryTime(LocalDateTime.now().minusDays(1).plusHours(2));
            booking3.setRating(5);
            booking3.setNotes("Express delivery completed successfully");
            bookingRepository.save(booking3);
        }
    }
    
    private void initializeTrips() {
        User driver = userRepository.findByEmail("driver@neurofleetx.com").orElse(null);
        Vehicle vehicle = vehicleRepository.findByVehicleId("FL-001").orElse(null);
        Booking booking = bookingRepository.findByBookingId("BK-001").orElse(null);

        if (driver != null && vehicle != null && booking != null) {
            // Sample Trip
            Trip trip1 = new Trip("TR-001", driver, vehicle, "Delhi Central Warehouse", 
                                "Connaught Place Hub", 18.5, 850.0);
            trip1.setBooking(booking);
            trip1.setStatus(Trip.TripStatus.IN_PROGRESS);
            trip1.setProgress(65);
            trip1.setStartTime(LocalDateTime.now().minusHours(1));
            trip1.setEstimatedCompletion(LocalDateTime.now().plusMinutes(25));
            trip1.setCustomerName("John Doe");
            trip1.setCustomerPhone("+91 98765 43210");
            tripRepository.save(trip1);
        }
    }
    
    private void initializeOptimizationData() {
        // Create additional bookings for optimization demonstration
        User customer = userRepository.findByEmail("customer@techsolutions.com").orElse(null);
        User customer2 = userRepository.findByEmail("jane@logistics.com").orElse(null);
        
        if (customer != null && customer2 != null) {
            // Batch of bookings for route optimization
            List<Booking> optimizationBookings = Arrays.asList(
                new Booking("BK-OPT-001", customer, "Delhi Central Hub", "Gurgaon Tech Park", 
                           28.5, 2100.0, 800.0, LocalDateTime.now().plusHours(4)),
                new Booking("BK-OPT-002", customer2, "Delhi Warehouse", "Noida Sector 18", 
                           32.1, 2400.0, 1200.0, LocalDateTime.now().plusHours(5)),
                new Booking("BK-OPT-003", customer, "Gurgaon Distribution", "Delhi Airport", 
                           24.8, 1800.0, 600.0, LocalDateTime.now().plusHours(6)),
                new Booking("BK-OPT-004", customer2, "Noida Depot", "Greater Noida Mall", 
                           18.2, 1500.0, 400.0, LocalDateTime.now().plusHours(7)),
                new Booking("BK-OPT-005", customer, "Delhi South", "Faridabad Industrial", 
                           35.6, 2800.0, 1500.0, LocalDateTime.now().plusHours(8))
            );
            
            for (Booking booking : optimizationBookings) {
                booking.setStatus(Booking.BookingStatus.SCHEDULED);
                booking.setEstimatedDelivery(booking.getScheduledDate().plusHours(2));
                bookingRepository.save(booking);
            }
        }
    }
    
    private void initializeMaintenanceData() {
        List<Vehicle> vehicles = vehicleRepository.findAll();
        
        for (Vehicle vehicle : vehicles) {
            // Create vehicle health metrics
            VehicleHealthMetrics metrics = new VehicleHealthMetrics(vehicle);
            
            // Set realistic health metrics based on vehicle status
            if (vehicle.getStatus() == Vehicle.VehicleStatus.MAINTENANCE) {
                // Poor health for maintenance vehicles
                metrics.setEngineTemperature(105.0); // High temperature
                metrics.setOilPressure(15.0); // Low oil pressure
                metrics.setOverallHealthScore(45.0);
                metrics.setHealthStatus(VehicleHealthMetrics.HealthStatus.POOR);
            } else if (vehicle.getBatteryLevel() != null && vehicle.getBatteryLevel() < 30) {
                // Fair health for low battery vehicles
                metrics.setEngineTemperature(88.0);
                metrics.setOilPressure(28.0);
                metrics.setOverallHealthScore(65.0);
                metrics.setHealthStatus(VehicleHealthMetrics.HealthStatus.FAIR);
            } else {
                // Good health for active vehicles
                metrics.setEngineTemperature(85.0);
                metrics.setOilPressure(35.0);
                metrics.setOverallHealthScore(88.0);
                metrics.setHealthStatus(VehicleHealthMetrics.HealthStatus.GOOD);
            }
            
            // Set common metrics
            metrics.setFrontLeftTirePressure(33.0);
            metrics.setFrontRightTirePressure(32.5);
            metrics.setRearLeftTirePressure(33.5);
            metrics.setRearRightTirePressure(33.0);
            metrics.setFuelLevel(vehicle.getFuelLevel() != null ? vehicle.getFuelLevel() : 75.0);
            metrics.setBatteryVoltage(12.6);
            metrics.setTransmissionTemperature(75.0);
            metrics.setBrakeFluidLevel(85.0);
            metrics.setVibrationLevel(0.5);
            metrics.setMileage(50000 + (int)(Math.random() * 100000));
            metrics.setCoolantLevel(80.0);
            
            healthMetricsRepository.save(metrics);
            
            // Create sample maintenance logs for vehicles with issues
            if (vehicle.getStatus() == Vehicle.VehicleStatus.MAINTENANCE) {
                MaintenanceLog log = new MaintenanceLog(vehicle, MaintenanceLog.MetricType.ENGINE_TEMPERATURE, 105.0);
                log.setAlert(true);
                log.setAlertSeverity(MaintenanceLog.AlertSeverity.HIGH);
                log.setAlertMessage("Engine temperature critical: 105.0°C");
                maintenanceLogRepository.save(log);
            }
        }
    }
}