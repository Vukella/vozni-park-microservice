package com.voznipark.travelorder.repository;

import com.voznipark.travelorder.entity.TravelOrderDriver;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TravelOrderDriverRepository extends JpaRepository<TravelOrderDriver, Long> {

    List<TravelOrderDriver> findByTravelOrderId(Long travelOrderId);

    List<TravelOrderDriver> findByDriverId(Long driverId);
}
