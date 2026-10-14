package com.voznipark.fleet.repository;

import com.voznipark.fleet.entity.RegistrationChangeLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RegistrationChangeLogRepository extends JpaRepository<RegistrationChangeLog, Long> {
    List<RegistrationChangeLog> findByVehicle_IdVehicleOrderByChangeDateDesc(Long vehicleId);
}