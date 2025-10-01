package com.neurofleetx.repository;

import com.neurofleetx.model.MaintenanceLog;
import com.neurofleetx.model.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MaintenanceLogRepository extends JpaRepository<MaintenanceLog, Long> {
    List<MaintenanceLog> findByVehicle(Vehicle vehicle);
    List<MaintenanceLog> findByVehicleId(Long vehicleId);
    List<MaintenanceLog> findByMetricType(MaintenanceLog.MetricType metricType);
    List<MaintenanceLog> findByAlertTrue();
    List<MaintenanceLog> findByAlertSeverity(MaintenanceLog.AlertSeverity severity);
    
    @Query("SELECT m FROM MaintenanceLog m WHERE m.vehicle.id = ?1 ORDER BY m.recordedAt DESC")
    List<MaintenanceLog> findByVehicleIdOrderByRecordedAtDesc(Long vehicleId);
    
    @Query("SELECT m FROM MaintenanceLog m WHERE m.alert = true ORDER BY m.recordedAt DESC")
    List<MaintenanceLog> findActiveAlertsOrderByRecordedAtDesc();
    
    @Query("SELECT m FROM MaintenanceLog m WHERE m.recordedAt >= ?1 AND m.recordedAt <= ?2")
    List<MaintenanceLog> findByRecordedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    @Query("SELECT m FROM MaintenanceLog m WHERE m.vehicle.id = ?1 AND m.metricType = ?2 ORDER BY m.recordedAt DESC")
    List<MaintenanceLog> findByVehicleIdAndMetricTypeOrderByRecordedAtDesc(Long vehicleId, MaintenanceLog.MetricType metricType);
}