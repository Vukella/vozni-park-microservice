package com.voznipark.fleet.repository;

import com.voznipark.fleet.entity.VehicleModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VehicleModelRepository extends JpaRepository<VehicleModel, Long> {

    List<VehicleModel> findByBrand_IdBrand(Long idBrand);
}
