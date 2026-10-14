package com.voznipark.fleet.repository;

import com.voznipark.fleet.entity.VehicleFault;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VehicleFaultRepository extends JpaRepository<VehicleFault, Long> {

    boolean existsByVehicle_IdVehicleAndVehicleInoperableTrueAndResolutionDateIsNull(Long idVehicle);

    List<VehicleFault> findByVehicle_IdVehicleAndResolutionDateIsNull(Long idVehicle);

    List<VehicleFault> findByVehicle_IdVehicleOrderByReportDateDesc(Long idVehicle);
}