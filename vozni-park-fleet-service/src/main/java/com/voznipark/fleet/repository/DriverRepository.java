package com.voznipark.fleet.repository;

import com.voznipark.fleet.entity.Driver;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DriverRepository extends JpaRepository<Driver, Long> {

    // EntityGraph fetch for DTO mapping

    @EntityGraph(attributePaths = {"medicalCertificate", "driverPermit"})
    @Query("SELECT d FROM Driver d")
    List<Driver> findAllWithRelations();

    @EntityGraph(attributePaths = {"medicalCertificate", "driverPermit"})
    @Query("SELECT d FROM Driver d WHERE d.idDriver = :id")
    Optional<Driver> findWithRelationsById(@Param("id") Long id);

    // Plain finders

    Optional<Driver> findBySapNumber(Integer sapNumber);

    boolean existsBySapNumber(Integer sapNumber);

    List<Driver> findByStatus(String status);

    List<Driver> findByFullNameContainingIgnoreCase(String name);

    // Location-aware queries

    @Query("""
            SELECT DISTINCT d FROM Driver d
            JOIN DriverLocation dl ON dl.driver = d
            WHERE dl.locationUnit.idLocationUnit IN :locationIds
            """)
    List<Driver> findByLocationIds(@Param("locationIds") List<Long> locationIds);

    @Query("""
            SELECT DISTINCT d FROM Driver d
            JOIN DriverLocation dl ON dl.driver = d
            WHERE dl.locationUnit.idLocationUnit = :locationId
            """)
    List<Driver> findByLocationId(@Param("locationId") Long locationId);

    @Query("""
            SELECT d FROM Driver d
            WHERE NOT EXISTS (
                SELECT dl FROM DriverLocation dl WHERE dl.driver = d
            )
            """)
    List<Driver> findDriversWithoutLocation();
}