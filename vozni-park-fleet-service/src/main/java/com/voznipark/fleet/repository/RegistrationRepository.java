package com.voznipark.fleet.repository;

import com.voznipark.fleet.entity.Registration;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RegistrationRepository extends JpaRepository<Registration, Long> {
}
