package com.voznipark.fleet.service;

import com.voznipark.fleet.entity.Driver;
import com.voznipark.fleet.entity.DriverLicenseAssignment;
import com.voznipark.fleet.entity.DriversLicense;
import com.voznipark.fleet.entity.SystemRefresh;
import com.voznipark.fleet.entity.TachographCard;
import com.voznipark.fleet.entity.Vehicle;
import com.voznipark.fleet.kafka.event.DocumentExpiringEvent;
import com.voznipark.fleet.kafka.producer.FleetEventProducer;
import com.voznipark.fleet.repository.DriverLicenseAssignmentRepository;
import com.voznipark.fleet.repository.DriverRepository;
import com.voznipark.fleet.repository.SystemRefreshRepository;
import com.voznipark.fleet.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Daily job at 02:00 — recalculates all statuses and publishes DocumentExpiring
 * events. Uses a pessimistic lock on system_refresh so it only runs once per day
 * even if multiple instances are running.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StatusRefreshService {

    private static final String REFRESH_KEY = "STATUS_REFRESH";
    private static final String RUN_BY = "fleet-service";

    private final VehicleRepository vehicleRepository;
    private final DriverRepository driverRepository;
    private final DriverLicenseAssignmentRepository driverLicenseAssignmentRepository;
    private final SystemRefreshRepository systemRefreshRepository;
    private final StatusCalculationService statusCalculationService;
    private final FleetEventProducer fleetEventProducer;

    @Value("${app.document-expiry.warning-days:30}")
    private int expiryWarningDays;

    @Scheduled(cron = "0 0 2 * * *")
    @Transactional
    public void scheduledRefresh() {
        performRefresh();
    }


    @Transactional
    public void triggerManualRefresh() {
        performRefresh();
    }

    // not @Transactional — runs inside the caller's already-open transaction
    private void performRefresh() {
        SystemRefresh lock = systemRefreshRepository.findByKeyNameWithLock(REFRESH_KEY)
                .orElseGet(() -> SystemRefresh.builder().keyName(REFRESH_KEY).build());

        LocalDate today = LocalDate.now();
        if (lock.getLastRunTs() != null && !lock.getLastRunTs().toLocalDate().isBefore(today)) {
            log.info("Status refresh already completed today ({}) — skipping", lock.getLastRunTs());
            return;
        }

        String status = "OK";
        String message;
        try {
            int vehiclesChecked = refreshVehicleStatuses();
            int driversChecked = refreshDriverStatuses();
            int expiringDocuments = checkExpiringDocuments();
            message = "vehicles=%d, drivers=%d, expiringDocuments=%d"
                    .formatted(vehiclesChecked, driversChecked, expiringDocuments);
            log.info("Status refresh completed: {}", message);
        } catch (Exception e) {
            status = "ERROR";
            message = e.getMessage();
            log.error("Status refresh failed", e);
        }

        lock.setLastRunTs(LocalDateTime.now());
        lock.setLastRunStatus(status);
        lock.setLastRunMessage(message);
        lock.setLastRunBy(RUN_BY);
        systemRefreshRepository.save(lock);
    }

    // Status recalculation

    private int refreshVehicleStatuses() {
        List<Vehicle> vehicles = vehicleRepository.findAll();
        int count = 0;
        for (Vehicle vehicle : vehicles) {
            if (Boolean.TRUE.equals(vehicle.getDecommissioned())) {
                continue;
            }
            statusCalculationService.recalculateAndSaveVehicleStatus(vehicle.getIdVehicle());
            count++;
        }
        return count;
    }

    private int refreshDriverStatuses() {
        List<Driver> drivers = driverRepository.findAll();
        for (Driver driver : drivers) {
            statusCalculationService.recalculateAndSaveDriverStatus(driver.getIdDriver());
        }
        return drivers.size();
    }

    // DocumentExpiring publishing

    private int checkExpiringDocuments() {
        int published = 0;
        LocalDate today = LocalDate.now();

        for (Vehicle vehicle : vehicleRepository.findAll()) {
            if (Boolean.TRUE.equals(vehicle.getDecommissioned())) {
                continue;
            }
            published += checkAndPublish("VEHICLE", vehicle.getIdVehicle(), "REGISTRATION",
                    vehicle.getRegistration() != null ? vehicle.getRegistration().getDateTo() : null, today);
            published += checkAndPublish("VEHICLE", vehicle.getIdVehicle(), "CASCO",
                    vehicle.getCascoInsurance() != null ? vehicle.getCascoInsurance().getDateTo() : null, today);
            published += checkAndPublish("VEHICLE", vehicle.getIdVehicle(), "TACHOGRAPH_DEVICE",
                    vehicle.getTachographDevice() != null ? vehicle.getTachographDevice().getDateTo() : null, today);
            published += checkAndPublish("VEHICLE", vehicle.getIdVehicle(), "FIRE_EXTINGUISHER",
                    vehicle.getFireExtinguisher() != null ? vehicle.getFireExtinguisher().getDateTo() : null, today);
            published += checkAndPublish("VEHICLE", vehicle.getIdVehicle(), "FIRST_AID_KIT",
                    vehicle.getFirstAidKit() != null ? vehicle.getFirstAidKit().getExpiryDate() : null, today);
        }

        for (Driver driver : driverRepository.findAll()) {
            published += checkAndPublish("DRIVER", driver.getIdDriver(), "MEDICAL_CERTIFICATE",
                    driver.getMedicalCertificate() != null ? driver.getMedicalCertificate().getDateTo() : null, today);
            published += checkAndPublish("DRIVER", driver.getIdDriver(), "DRIVER_PERMIT",
                    driver.getDriverPermit() != null ? driver.getDriverPermit().getDateTo() : null, today);

            List<DriverLicenseAssignment> assignments =
                    driverLicenseAssignmentRepository.findByDriver_IdDriver(driver.getIdDriver());

            for (DriverLicenseAssignment assignment : assignments) {
                DriversLicense license = assignment.getDriversLicense();
                if (license == null) {
                    continue;
                }
                published += checkAndPublish("DRIVER", driver.getIdDriver(), "DRIVERS_LICENSE",
                        license.getDateTo(), today);

                TachographCard card = license.getTachographCard();
                if (card != null) {
                    published += checkAndPublish("DRIVER", driver.getIdDriver(), "TACHOGRAPH_CARD",
                            card.getDateTo(), today);
                }
            }
        }

        return published;
    }

    // publishes event if expiry falls within the warning window
    private int checkAndPublish(String entityType, Long entityId, String documentType,
                                 LocalDate dateTo, LocalDate today) {
        if (dateTo == null || dateTo.isBefore(today)) {
            return 0;
        }

        long daysUntilExpiry = ChronoUnit.DAYS.between(today, dateTo);
        if (daysUntilExpiry > expiryWarningDays) {
            return 0;
        }

        DocumentExpiringEvent event = DocumentExpiringEvent.builder()
                .entityType(entityType)
                .entityId(entityId)
                .documentType(documentType)
                .expiryDate(dateTo)
                .daysUntilExpiry((int) daysUntilExpiry)
                .locationId(null)
                .occurredAt(Instant.now())
                .build();

        fleetEventProducer.publishDocumentExpiring(event);
        return 1;
    }
}
