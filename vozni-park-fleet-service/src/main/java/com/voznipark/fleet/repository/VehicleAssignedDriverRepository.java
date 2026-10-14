package com.voznipark.fleet.repository;

import com.voznipark.fleet.entity.VehicleAssignedDriver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VehicleAssignedDriverRepository extends JpaRepository<VehicleAssignedDriver, Long> {
    Optional<VehicleAssignedDriver> findByVehicle_IdVehicle(Long vehicleId);
    Optional<VehicleAssignedDriver> findByDriver_IdDriver(Long driverId);
}