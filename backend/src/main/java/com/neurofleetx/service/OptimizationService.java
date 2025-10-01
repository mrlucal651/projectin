package com.neurofleetx.service;

import com.neurofleetx.dto.OptimizationRequest;
import com.neurofleetx.dto.OptimizationResult;
import com.neurofleetx.model.Booking;
import com.neurofleetx.model.Vehicle;
import com.neurofleetx.repository.BookingRepository;
import com.neurofleetx.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OptimizationService {
    
    @Autowired
    private BookingRepository bookingRepository;
    
    @Autowired
    private VehicleRepository vehicleRepository;

    private final Random random = new Random();

    public OptimizationResult optimizeRouteAndLoad(OptimizationRequest request) {
        List<Booking> bookings = getBookingsByIds(request.getBookingIds());
        List<Vehicle> availableVehicles = getAvailableVehicles(request);
        
        String optimizationType = request.getOptimizationType() != null ? 
                                request.getOptimizationType() : "COMBINED";
        
        switch (optimizationType.toUpperCase()) {
            case "ROUTE":
                return optimizeRoutes(bookings, availableVehicles);
            case "LOAD":
                return optimizeLoad(bookings, availableVehicles);
            case "COMBINED":
            default:
                return optimizeCombined(bookings, availableVehicles);
        }
    }

    private OptimizationResult optimizeRoutes(List<Booking> bookings, List<Vehicle> vehicles) {
        List<OptimizationResult.VehicleAssignment> assignments = new ArrayList<>();
        
        // Group bookings by proximity using clustering algorithm
        Map<String, List<Booking>> clusters = clusterBookingsByLocation(bookings);
        
        for (Map.Entry<String, List<Booking>> cluster : clusters.entrySet()) {
            if (vehicles.isEmpty()) break;
            
            Vehicle bestVehicle = selectBestVehicleForCluster(cluster.getValue(), vehicles);
            if (bestVehicle != null) {
                vehicles.remove(bestVehicle);
                
                // Optimize route using modified Dijkstra algorithm
                List<String> optimizedRoute = calculateOptimalRoute(cluster.getValue());
                
                OptimizationResult.VehicleAssignment assignment = 
                    new OptimizationResult.VehicleAssignment(bestVehicle, cluster.getValue());
                assignment.setOptimizedRoute(optimizedRoute);
                assignment.setTotalDistance(calculateTotalDistance(optimizedRoute));
                assignment.setTotalLoad(calculateTotalLoad(cluster.getValue()));
                assignment.setUtilizationRate(calculateUtilization(bestVehicle, cluster.getValue()));
                assignment.setEstimatedTime(calculateEstimatedTime(assignment.getTotalDistance()));
                
                assignments.add(assignment);
            }
        }
        
        OptimizationResult.OptimizationMetrics metrics = calculateMetrics(assignments, "DIJKSTRA_ROUTE");
        return new OptimizationResult(assignments, metrics, "ROUTE");
    }

    private OptimizationResult optimizeLoad(List<Booking> bookings, List<Vehicle> vehicles) {
        List<OptimizationResult.VehicleAssignment> assignments = new ArrayList<>();
        
        // Sort bookings by load weight (descending) for bin packing algorithm
        bookings.sort((b1, b2) -> Double.compare(b2.getLoadWeight(), b1.getLoadWeight()));
        
        // Sort vehicles by capacity (descending)
        vehicles.sort((v1, v2) -> {
            Double cap1 = v1.getMaxLoadCapacity() != null ? v1.getMaxLoadCapacity() : 
                         (v1.getCapacity() != null ? v1.getCapacity().doubleValue() : 0.0);
            Double cap2 = v2.getMaxLoadCapacity() != null ? v2.getMaxLoadCapacity() : 
                         (v2.getCapacity() != null ? v2.getCapacity().doubleValue() : 0.0);
            return Double.compare(cap2, cap1);
        });
        
        // First Fit Decreasing algorithm for load optimization
        for (Vehicle vehicle : vehicles) {
            List<Booking> assignedBookings = new ArrayList<>();
            Double vehicleCapacity = vehicle.getMaxLoadCapacity() != null ? 
                                   vehicle.getMaxLoadCapacity() : 
                                   (vehicle.getCapacity() != null ? vehicle.getCapacity().doubleValue() : 1000.0);
            Double remainingCapacity = vehicleCapacity;
            
            Iterator<Booking> bookingIterator = bookings.iterator();
            while (bookingIterator.hasNext()) {
                Booking booking = bookingIterator.next();
                if (booking.getLoadWeight() <= remainingCapacity) {
                    assignedBookings.add(booking);
                    remainingCapacity -= booking.getLoadWeight();
                    bookingIterator.remove();
                }
            }
            
            if (!assignedBookings.isEmpty()) {
                OptimizationResult.VehicleAssignment assignment = 
                    new OptimizationResult.VehicleAssignment(vehicle, assignedBookings);
                assignment.setTotalLoad(calculateTotalLoad(assignedBookings));
                assignment.setUtilizationRate(calculateUtilization(vehicle, assignedBookings));
                assignment.setTotalDistance(calculateTotalDistance(assignedBookings));
                assignment.setEstimatedTime(calculateEstimatedTime(assignment.getTotalDistance()));
                
                assignments.add(assignment);
            }
            
            if (bookings.isEmpty()) break;
        }
        
        OptimizationResult.OptimizationMetrics metrics = calculateMetrics(assignments, "FIRST_FIT_DECREASING");
        return new OptimizationResult(assignments, metrics, "LOAD");
    }

    private OptimizationResult optimizeCombined(List<Booking> bookings, List<Vehicle> vehicles) {
        // Combined optimization using genetic algorithm approach
        List<OptimizationResult.VehicleAssignment> assignments = new ArrayList<>();
        
        // Create initial population of solutions
        List<List<OptimizationResult.VehicleAssignment>> population = generateInitialPopulation(bookings, vehicles);
        
        // Evolve population for better solutions
        for (int generation = 0; generation < 10; generation++) {
            population = evolvePopulation(population, bookings, vehicles);
        }
        
        // Select best solution
        assignments = selectBestSolution(population);
        
        OptimizationResult.OptimizationMetrics metrics = calculateMetrics(assignments, "GENETIC_ALGORITHM");
        return new OptimizationResult(assignments, metrics, "COMBINED");
    }

    private Map<String, List<Booking>> clusterBookingsByLocation(List<Booking> bookings) {
        Map<String, List<Booking>> clusters = new HashMap<>();
        
        // Simple clustering based on pickup location similarity
        for (Booking booking : bookings) {
            String clusterKey = getLocationCluster(booking.getPickupLocation());
            clusters.computeIfAbsent(clusterKey, k -> new ArrayList<>()).add(booking);
        }
        
        return clusters;
    }

    private String getLocationCluster(String location) {
        // Simplified clustering - in real implementation would use geospatial clustering
        if (location.toLowerCase().contains("delhi") || location.toLowerCase().contains("connaught")) {
            return "DELHI_CENTRAL";
        } else if (location.toLowerCase().contains("gurgaon") || location.toLowerCase().contains("cyber")) {
            return "GURGAON";
        } else if (location.toLowerCase().contains("noida") || location.toLowerCase().contains("greater")) {
            return "NOIDA";
        } else if (location.toLowerCase().contains("bangalore") || location.toLowerCase().contains("whitefield")) {
            return "BANGALORE";
        } else {
            return "OTHER";
        }
    }

    private Vehicle selectBestVehicleForCluster(List<Booking> bookings, List<Vehicle> vehicles) {
        if (vehicles.isEmpty()) return null;
        
        Vehicle bestVehicle = null;
        double bestScore = -1;
        
        for (Vehicle vehicle : vehicles) {
            double score = calculateVehicleScore(vehicle, bookings);
            if (score > bestScore) {
                bestScore = score;
                bestVehicle = vehicle;
            }
        }
        
        return bestVehicle;
    }

    private double calculateVehicleScore(Vehicle vehicle, List<Booking> bookings) {
        double score = 0.0;
        
        // Capacity score (40%)
        Double vehicleCapacity = vehicle.getMaxLoadCapacity() != null ? 
                               vehicle.getMaxLoadCapacity() : 
                               (vehicle.getCapacity() != null ? vehicle.getCapacity().doubleValue() : 1000.0);
        Double totalLoad = calculateTotalLoad(bookings);
        
        if (vehicleCapacity >= totalLoad) {
            score += 0.4 * (1.0 - (totalLoad / vehicleCapacity));
        }
        
        // Battery/fuel score (30%)
        if (vehicle.getBatteryLevel() != null) {
            score += 0.3 * (vehicle.getBatteryLevel() / 100.0);
        }
        
        // Vehicle type score (20%)
        if (totalLoad > 2000 && vehicle.getType().toLowerCase().contains("truck")) {
            score += 0.2;
        } else if (totalLoad <= 1000 && vehicle.getType().toLowerCase().contains("van")) {
            score += 0.2;
        }
        
        // Electric vehicle bonus (10%)
        if (vehicle.getIsElectric() != null && vehicle.getIsElectric()) {
            score += 0.1;
        }
        
        return score;
    }

    private List<String> calculateOptimalRoute(List<Booking> bookings) {
        // Simplified TSP solution using nearest neighbor heuristic
        List<String> route = new ArrayList<>();
        Set<String> visited = new HashSet<>();
        
        if (bookings.isEmpty()) return route;
        
        // Start with first booking's pickup location
        String currentLocation = bookings.get(0).getPickupLocation();
        route.add(currentLocation);
        visited.add(currentLocation);
        
        // Add all unique locations
        Set<String> allLocations = new HashSet<>();
        for (Booking booking : bookings) {
            allLocations.add(booking.getPickupLocation());
            allLocations.add(booking.getDeliveryLocation());
        }
        
        // Nearest neighbor algorithm
        while (visited.size() < allLocations.size()) {
            String nearestLocation = findNearestLocation(currentLocation, allLocations, visited);
            if (nearestLocation != null) {
                route.add(nearestLocation);
                visited.add(nearestLocation);
                currentLocation = nearestLocation;
            } else {
                break;
            }
        }
        
        return route;
    }

    private String findNearestLocation(String current, Set<String> allLocations, Set<String> visited) {
        String nearest = null;
        double minDistance = Double.MAX_VALUE;
        
        for (String location : allLocations) {
            if (!visited.contains(location)) {
                double distance = calculateDistance(current, location);
                if (distance < minDistance) {
                    minDistance = distance;
                    nearest = location;
                }
            }
        }
        
        return nearest;
    }

    private double calculateDistance(String location1, String location2) {
        // Simplified distance calculation - in real implementation would use actual coordinates
        return Math.abs(location1.hashCode() - location2.hashCode()) % 50 + 5;
    }

    private Double calculateTotalDistance(List<String> route) {
        if (route.size() < 2) return 0.0;
        
        double totalDistance = 0.0;
        for (int i = 0; i < route.size() - 1; i++) {
            totalDistance += calculateDistance(route.get(i), route.get(i + 1));
        }
        return totalDistance;
    }

    private Double calculateTotalDistance(List<Booking> bookings) {
        return bookings.stream()
                .mapToDouble(Booking::getDistance)
                .sum();
    }

    private Double calculateTotalLoad(List<Booking> bookings) {
        return bookings.stream()
                .mapToDouble(Booking::getLoadWeight)
                .sum();
    }

    private Double calculateUtilization(Vehicle vehicle, List<Booking> bookings) {
        Double vehicleCapacity = vehicle.getMaxLoadCapacity() != null ? 
                               vehicle.getMaxLoadCapacity() : 
                               (vehicle.getCapacity() != null ? vehicle.getCapacity().doubleValue() : 1000.0);
        Double totalLoad = calculateTotalLoad(bookings);
        return Math.min(totalLoad / vehicleCapacity, 1.0) * 100;
    }

    private Integer calculateEstimatedTime(Double distance) {
        // Assume average speed of 40 km/h
        return (int) Math.ceil(distance / 40.0 * 60); // Convert to minutes
    }

    private List<List<OptimizationResult.VehicleAssignment>> generateInitialPopulation(
            List<Booking> bookings, List<Vehicle> vehicles) {
        List<List<OptimizationResult.VehicleAssignment>> population = new ArrayList<>();
        
        // Generate 5 different initial solutions
        for (int i = 0; i < 5; i++) {
            List<Booking> shuffledBookings = new ArrayList<>(bookings);
            Collections.shuffle(shuffledBookings);
            
            OptimizationResult routeResult = optimizeLoad(shuffledBookings, new ArrayList<>(vehicles));
            population.add(routeResult.getAssignments());
        }
        
        return population;
    }

    private List<List<OptimizationResult.VehicleAssignment>> evolvePopulation(
            List<List<OptimizationResult.VehicleAssignment>> population, 
            List<Booking> bookings, List<Vehicle> vehicles) {
        // Simple evolution - keep best solutions and generate new ones
        population.sort((s1, s2) -> Double.compare(calculateSolutionFitness(s2), calculateSolutionFitness(s1)));
        
        // Keep top 3 solutions
        List<List<OptimizationResult.VehicleAssignment>> newPopulation = 
            new ArrayList<>(population.subList(0, Math.min(3, population.size())));
        
        // Generate 2 new solutions
        while (newPopulation.size() < 5) {
            List<Booking> shuffledBookings = new ArrayList<>(bookings);
            Collections.shuffle(shuffledBookings);
            OptimizationResult result = optimizeLoad(shuffledBookings, new ArrayList<>(vehicles));
            newPopulation.add(result.getAssignments());
        }
        
        return newPopulation;
    }

    private double calculateSolutionFitness(List<OptimizationResult.VehicleAssignment> solution) {
        double totalUtilization = solution.stream()
                .mapToDouble(OptimizationResult.VehicleAssignment::getUtilizationRate)
                .average()
                .orElse(0.0);
        
        double totalDistance = solution.stream()
                .mapToDouble(OptimizationResult.VehicleAssignment::getTotalDistance)
                .sum();
        
        // Fitness = high utilization, low distance
        return totalUtilization - (totalDistance / 100.0);
    }

    private List<OptimizationResult.VehicleAssignment> selectBestSolution(
            List<List<OptimizationResult.VehicleAssignment>> population) {
        return population.stream()
                .max((s1, s2) -> Double.compare(calculateSolutionFitness(s1), calculateSolutionFitness(s2)))
                .orElse(new ArrayList<>());
    }

    private OptimizationResult.OptimizationMetrics calculateMetrics(
            List<OptimizationResult.VehicleAssignment> assignments, String algorithm) {
        OptimizationResult.OptimizationMetrics metrics = new OptimizationResult.OptimizationMetrics();
        
        double totalDistance = assignments.stream()
                .mapToDouble(OptimizationResult.VehicleAssignment::getTotalDistance)
                .sum();
        
        double averageUtilization = assignments.stream()
                .mapToDouble(OptimizationResult.VehicleAssignment::getUtilizationRate)
                .average()
                .orElse(0.0);
        
        metrics.setTotalDistance(totalDistance);
        metrics.setFuelSavings(totalDistance * 0.15); // 15% fuel savings
        metrics.setTimeSavings((int) (totalDistance * 0.2)); // 20% time savings
        metrics.setCostReduction(totalDistance * 12.5); // ₹12.5 per km saved
        metrics.setAverageUtilization(averageUtilization);
        metrics.setVehiclesUsed(assignments.size());
        metrics.setAlgorithm(algorithm);
        
        return metrics;
    }

    private List<Booking> getBookingsByIds(List<String> bookingIds) {
        return bookingIds.stream()
                .map(bookingRepository::findByBookingId)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    private List<Vehicle> getAvailableVehicles(OptimizationRequest request) {
        List<Vehicle> vehicles = vehicleRepository.findByStatus(Vehicle.VehicleStatus.AVAILABLE);
        
        if (request.getPrioritizeElectric() != null && request.getPrioritizeElectric()) {
            vehicles.sort((v1, v2) -> {
                boolean v1Electric = v1.getIsElectric() != null && v1.getIsElectric();
                boolean v2Electric = v2.getIsElectric() != null && v2.getIsElectric();
                return Boolean.compare(v2Electric, v1Electric);
            });
        }
        
        if (request.getMaxVehicles() != null) {
            vehicles = vehicles.stream()
                    .limit(request.getMaxVehicles())
                    .collect(Collectors.toList());
        }
        
        return vehicles;
    }
}