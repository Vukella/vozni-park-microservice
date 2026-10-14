package com.voznipark.fleet.repository;

import com.voznipark.fleet.entity.VehicleSubtype;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VehicleSubtypeRepository extends JpaRepository<VehicleSubtype, Long> {

    List<VehicleSubtype> findByVehicleType_IdVehicleType(Long idVehicleType);
}
