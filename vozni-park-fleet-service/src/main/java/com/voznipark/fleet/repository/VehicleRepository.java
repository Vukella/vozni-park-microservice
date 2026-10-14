package com.voznipark.fleet.repository;

import com.voznipark.fleet.entity.Vehicle;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    Optional<Vehicle> findBySapNumber(Integer sapNumber);

    /** Fetches all 11 relations in one query — avoids N+1 when the mapper calls getters. */
    @EntityGraph(attributePaths = {
            "registration", "firstAidKit", "tachographDevice", "cascoInsurance", "fireExtinguisher",
            "fuelType", "vehicleModel", "vehicleModel.brand", "vehicleType",
            "vehicleSubtype", "vehicleSubtype.vehicleType", "nisCard", "tireMarking"
    })
    @Query("SELECT v FROM Vehicle v")
    List<Vehicle> findAllWithRelations();

    @EntityGraph(attributePaths = {
            "registration", "firstAidKit", "tachographDevice", "cascoInsurance", "fireExtinguisher",
            "fuelType", "vehicleModel", "vehicleModel.brand", "vehicleType",
            "vehicleSubtype", "vehicleSubtype.vehicleType", "nisCard", "tireMarking"
    })
    @Query("SELECT v FROM Vehicle v WHERE v.idVehicle = :id")
    Optional<Vehicle> findByIdWithRelations(@Param("id") Long id);
}