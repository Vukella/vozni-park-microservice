package com.voznipark.fleet.repository;

import com.voznipark.fleet.entity.DriverLicenseAssignment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DriverLicenseAssignmentRepository extends JpaRepository<DriverLicenseAssignment, Long> {

    List<DriverLicenseAssignment> findByDriver_IdDriver(Long idDriver);
}
