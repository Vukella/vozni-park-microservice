package com.voznipark.maintenance.repository;

import com.voznipark.maintenance.entity.VehicleMileageTracker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface VehicleMileageTrackerRepository extends JpaRepository<VehicleMileageTracker, Long> {
}
