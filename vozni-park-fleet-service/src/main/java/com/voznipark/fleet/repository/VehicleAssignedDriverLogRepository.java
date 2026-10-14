package com.voznipark.fleet.repository;

import com.voznipark.fleet.entity.VehicleAssignedDriverLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VehicleAssignedDriverLogRepository extends JpaRepository<VehicleAssignedDriverLog, Long> {
    List<VehicleAssignedDriverLog> findByVehicle_IdVehicleOrderByChangeDateDesc(Long vehicleId);
}