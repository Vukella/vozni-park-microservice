package com.voznipark.fleet.service;

import com.voznipark.fleet.entity.Driver;
import com.voznipark.fleet.entity.DriverLicenseAssignment;
import com.voznipark.fleet.entity.DriversLicense;
import com.voznipark.fleet.entity.TachographCard;
import com.voznipark.fleet.entity.Vehicle;
import com.voznipark.fleet.enums.DriverStatus;
import com.voznipark.fleet.enums.VehicleStatus;
import com.voznipark.fleet.repository.DriverLicenseAssignmentRepository;
import com.voznipark.fleet.repository.DriverRepository;
import com.voznipark.fleet.repository.VehicleFaultRepository;
import com.voznipark.fleet.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.voznipark.fleet.repository.ResourceReservationRepository;

import java.time.LocalDate;
import java.util.List;

/**
 * Single source of truth for vehicle and driver status.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatusCalculationService {

    private final VehicleRepository vehicleRepository;
    private final DriverRepository driverRepository;
    private final VehicleFaultRepository vehicleFaultRepository;
    private final DriverLicenseAssignmentRepository driverLicenseAssignmentRepository;
    private final ResourceReservationRepository resourceReservationRepository;

    // Vehicle status

    /**
     * Priority: IN_FAULT > INVALID > ON_DUTY > AVAILABLE.
     * UNAVAILABLE (overdue travel order) is not computed here.
     */
    public VehicleStatus calculateVehicleStatus(Vehicle vehicle) {
        if (hasActiveFault(vehicle)) {
            return VehicleStatus.IN_FAULT;
        }
        if (hasExpiredDocument(vehicle)) {
            return VehicleStatus.INVALID;
        }
        if (resourceReservationRepository.existsByResourceTypeAndResourceIdAndReleasedAtIsNull(
                "VEHICLE", vehicle.getIdVehicle())) {
            return VehicleStatus.ON_DUTY;
        }
        return VehicleStatus.AVAILABLE;
    }

    private boolean hasActiveFault(Vehicle vehicle) {
        return vehicleFaultRepository
                .existsByVehicle_IdVehicleAndVehicleInoperableTrueAndResolutionDateIsNull(vehicle.getIdVehicle());
    }

    // registration, casco, tachograph device, fire extinguisher, first aid kit
    private boolean hasExpiredDocument(Vehicle vehicle) {
        LocalDate today = LocalDate.now();
        return isExpired(vehicle.getRegistration() != null ? vehicle.getRegistration().getDateTo() : null, today)
                || isExpired(vehicle.getCascoInsurance() != null ? vehicle.getCascoInsurance().getDateTo() : null, today)
                || isExpired(vehicle.getTachographDevice() != null ? vehicle.getTachographDevice().getDateTo() : null, today)
                || isExpired(vehicle.getFireExtinguisher() != null ? vehicle.getFireExtinguisher().getDateTo() : null, today)
                || isExpired(vehicle.getFirstAidKit() != null ? vehicle.getFirstAidKit().getExpiryDate() : null, today);
    }

    // Driver status

    /**
     * Priority: SUSPENDED > INVALID_DOCUMENTS > ON_DUTY > AVAILABLE.
     * UNAVAILABLE (overdue travel order) is not computed here.
     */
    public DriverStatus calculateDriverStatus(Driver driver) {
        if (Boolean.TRUE.equals(driver.getLicenseRevoked())) {
            return DriverStatus.SUSPENDED;
        }
        if (hasExpiredDocument(driver)) {
            return DriverStatus.INVALID_DOCUMENTS;
        }
        if (resourceReservationRepository.existsByResourceTypeAndResourceIdAndReleasedAtIsNull(
                "DRIVER", driver.getIdDriver())) {
            return DriverStatus.ON_DUTY;
        }
        return DriverStatus.AVAILABLE;
    }

    private boolean hasExpiredDocument(Driver driver) {
        LocalDate today = LocalDate.now();

        if (isExpired(driver.getMedicalCertificate() != null ? driver.getMedicalCertificate().getDateTo() : null, today)) {
            return true;
        }
        if (isExpired(driver.getDriverPermit() != null ? driver.getDriverPermit().getDateTo() : null, today)) {
            return true;
        }

        List<DriverLicenseAssignment> assignments =
                driverLicenseAssignmentRepository.findByDriver_IdDriver(driver.getIdDriver());

        for (DriverLicenseAssignment assignment : assignments) {
            DriversLicense license = assignment.getDriversLicense();
            if (license == null) {
                continue;
            }
            if (isExpired(license.getDateTo(), today)) {
                return true;
            }
            TachographCard card = license.getTachographCard();
            if (card != null && isExpired(card.getDateTo(), today)) {
                return true;
            }
        }
        return false;
    }

    private boolean isExpired(LocalDate dateTo, LocalDate today) {
        return dateTo != null && dateTo.isBefore(today);
    }

    // Persist methods

    /** Skips decommissioned vehicles. */
    @Transactional
    public void recalculateAndSaveVehicleStatus(Long vehicleId) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found: " + vehicleId));

        if (Boolean.TRUE.equals(vehicle.getDecommissioned())) {
            return;
        }

        VehicleStatus status = calculateVehicleStatus(vehicle);
        vehicle.setVehicleStatus(status.name());
        vehicle.setStatusCode(status.getCode());
        vehicleRepository.save(vehicle);
    }

    @Transactional
    public void recalculateAndSaveDriverStatus(Long driverId) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new IllegalArgumentException("Driver not found: " + driverId));

        DriverStatus status = calculateDriverStatus(driver);
        driver.setStatus(status.name());
        driver.setStatusCode(status.getCode());
        driverRepository.save(driver);
    }
}