package com.voznipark.fleet.repository;

import com.voznipark.fleet.entity.VehicleType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VehicleTypeRepository extends JpaRepository<VehicleType, Long> {
}
