package com.voznipark.fleet.repository;

import com.voznipark.fleet.entity.DriverLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DriverLocationRepository extends JpaRepository<DriverLocation, Long> {
    Optional<DriverLocation> findByDriver_IdDriver(Long driverId);
    List<DriverLocation> findByLocationUnit_IdLocationUnit(Long locationUnitId);
}