package com.voznipark.fleet.repository;

import com.voznipark.fleet.entity.LicenseRevocation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LicenseRevocationRepository extends JpaRepository<LicenseRevocation, Long> {

    Optional<LicenseRevocation> findByDriver_IdDriverAndRestorationDateIsNull(Long idDriver);
}
