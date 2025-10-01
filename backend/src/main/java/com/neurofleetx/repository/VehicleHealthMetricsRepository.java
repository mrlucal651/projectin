package com.neurofleetx.repository;

import com.neurofleetx.model.VehicleHealthMetrics;
import com.neurofleetx.model.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface VehicleHealthMetricsRepository extends JpaRepository<VehicleHealthMetrics, Long> {
    Optional<VehicleHealthMetrics> findByVehicle(Vehicle vehicle);
    Optional<VehicleHealthMetrics> findByVehicleId(Long vehicleId);
    List<VehicleHealthMetrics> findByHealthStatus(VehicleHealthMetrics.HealthStatus status);
    
    @Query("SELECT v FROM VehicleHealthMetrics v WHERE v.overallHealthScore < ?1")
    List<VehicleHealthMetrics> findByOverallHealthScoreLessThan(Double score);
    
    @Query("SELECT v FROM VehicleHealthMetrics v ORDER BY v.overallHealthScore ASC")
    List<VehicleHealthMetrics> findAllOrderByHealthScoreAsc();
    
    @Query("SELECT AVG(v.overallHealthScore) FROM VehicleHealthMetrics v")
    Double getAverageFleetHealthScore();
}