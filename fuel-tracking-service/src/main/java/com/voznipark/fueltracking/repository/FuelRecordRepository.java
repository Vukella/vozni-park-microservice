package com.voznipark.fueltracking.repository;

import com.voznipark.fueltracking.dto.response.FuelSummaryDTO;
import com.voznipark.fueltracking.entity.FuelRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FuelRecordRepository extends JpaRepository<FuelRecord, Long> {

    boolean existsByTravelOrderId(Long travelOrderId);

    List<FuelRecord> findByVehicleId(Long vehicleId);

    List<FuelRecord> findAllByOrderByRecordedAtDesc();

    @Query("SELECT new com.voznipark.fueltracking.dto.response.FuelSummaryDTO(" +
            "COUNT(f), SUM(f.distanceKm), SUM(f.fuelConsumedLiters), SUM(f.totalFuelCost)) " +
            "FROM FuelRecord f")
    FuelSummaryDTO getSummary();
}