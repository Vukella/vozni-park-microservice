package com.voznipark.fleet.repository;

import com.voznipark.fleet.entity.TrafficAccident;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TrafficAccidentRepository extends JpaRepository<TrafficAccident, Long> {
    List<TrafficAccident> findByVehicle_IdVehicleOrderByAccidentDateDesc(Long vehicleId);
    List<TrafficAccident> findByDriver_IdDriverOrderByAccidentDateDesc(Long driverId);
}