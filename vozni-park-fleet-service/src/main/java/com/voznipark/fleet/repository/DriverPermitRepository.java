package com.voznipark.fleet.repository;

import com.voznipark.fleet.entity.DriverPermit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DriverPermitRepository extends JpaRepository<DriverPermit, Long> {
}
