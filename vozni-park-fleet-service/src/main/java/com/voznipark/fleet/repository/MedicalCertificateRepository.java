package com.voznipark.fleet.repository;

import com.voznipark.fleet.entity.MedicalCertificate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MedicalCertificateRepository extends JpaRepository<MedicalCertificate, Long> {
}
