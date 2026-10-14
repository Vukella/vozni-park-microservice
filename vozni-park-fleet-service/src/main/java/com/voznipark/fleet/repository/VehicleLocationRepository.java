package com.voznipark.fleet.repository;

import com.voznipark.fleet.entity.VehicleLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VehicleLocationRepository extends JpaRepository<VehicleLocation, Long> {
    Optional<VehicleLocation> findByVehicle_IdVehicle(Long vehicleId);
    List<VehicleLocation> findByLocationUnit_IdLocationUnit(Long locationUnitId);
}