package com.voznipark.fleet.repository;

import com.voznipark.fleet.entity.DriversLicense;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DriversLicenseRepository extends JpaRepository<DriversLicense, Long> {
}
