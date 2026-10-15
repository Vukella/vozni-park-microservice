package com.voznipark.fleet.service;

import com.voznipark.fleet.dto.request.DriverRequestDTO;
import com.voznipark.fleet.dto.response.DriverResponseDTO;
import com.voznipark.fleet.entity.Driver;
import com.voznipark.fleet.exception.DriverNotAvailableException;
import com.voznipark.fleet.exception.DuplicateEntityException;
import com.voznipark.fleet.exception.EntityNotFoundException;
import com.voznipark.fleet.mapper.DriverMapper;
import com.voznipark.fleet.repository.DriverPermitRepository;
import com.voznipark.fleet.repository.DriverRepository;
import com.voznipark.fleet.repository.MedicalCertificateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.voznipark.fleet.entity.ResourceReservation;
import com.voznipark.fleet.repository.ResourceReservationRepository;
import java.time.Instant;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class DriverService {

    private final DriverRepository driverRepository;
    private final DriverMapper driverMapper;
    private final StatusCalculationService statusCalculationService;
    private final MedicalCertificateRepository medicalCertificateRepository;
    private final DriverPermitRepository driverPermitRepository;
    private final ResourceReservationRepository reservationRepository;

    // Queries

    public List<DriverResponseDTO> getAllDrivers() {
        return driverMapper.toResponseDTOList(driverRepository.findAllWithRelations());
    }

    public DriverResponseDTO getDriverById(Long id) {
        return driverRepository.findWithRelationsById(id)
                .map(driverMapper::toResponseDTO)
                .orElseThrow(() -> new EntityNotFoundException("Driver", id));
    }

    public List<DriverResponseDTO> getDriversByStatus(String status) {
        return driverMapper.toResponseDTOList(driverRepository.findByStatus(status));
    }

    public List<DriverResponseDTO> getDriversByLocation(Long locationId) {
        return driverMapper.toResponseDTOList(driverRepository.findByLocationId(locationId));
    }

    public List<DriverResponseDTO> searchDriversByName(String name) {
        return driverMapper.toResponseDTOList(
                driverRepository.findByFullNameContainingIgnoreCase(name));
    }

    // Mutations

    @Transactional
    public DriverResponseDTO createDriver(DriverRequestDTO dto) {
        if (driverRepository.existsBySapNumber(dto.sapNumber())) {
            throw new DuplicateEntityException("Driver with SAP number " + dto.sapNumber() + " already exists");
        }

        Driver driver = driverMapper.toEntity(dto);
        applyRelations(driver, dto);

        Driver saved = driverRepository.save(driver);
        statusCalculationService.recalculateAndSaveDriverStatus(saved.getIdDriver());

        return driverRepository.findWithRelationsById(saved.getIdDriver())
                .map(driverMapper::toResponseDTO)
                .orElseThrow();
    }

    @Transactional
    public DriverResponseDTO updateDriver(Long id, DriverRequestDTO dto) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Driver", id));

        if (!dto.sapNumber().equals(driver.getSapNumber())
                && driverRepository.existsBySapNumber(dto.sapNumber())) {
            throw new DuplicateEntityException("Driver with SAP number " + dto.sapNumber() + " already exists");
        }

        driverMapper.updateEntity(driver, dto);
        applyRelations(driver, dto);
        driverRepository.save(driver);

        statusCalculationService.recalculateAndSaveDriverStatus(id);

        return driverRepository.findWithRelationsById(id)
                .map(driverMapper::toResponseDTO)
                .orElseThrow();
    }

    @Transactional
    public void deleteDriver(Long id) {
        if (!driverRepository.existsById(id)) {
            throw new EntityNotFoundException("Driver", id);
        }
        driverRepository.deleteById(id);
    }

    // Helpers

    private void applyRelations(Driver driver, DriverRequestDTO dto) {
        driver.setMedicalCertificate(
                dto.medicalCertificateId() != null
                        ? medicalCertificateRepository.getReferenceById(dto.medicalCertificateId())
                        : null
        );
        driver.setDriverPermit(
                dto.driverPermitId() != null
                        ? driverPermitRepository.getReferenceById(dto.driverPermitId())
                        : null
        );
    }

    // Saga reservation methods

    /**
     * Reserves all specified drivers for the saga.
     * All must be AVAILABLE — fails the whole group if any are not.
     */
    @Transactional
    public void reserveDrivers(List<Long> driverIds, Long travelOrderId) {
        if (driverIds == null || driverIds.isEmpty()) {
            throw new DriverNotAvailableException("Travel order " + travelOrderId + " has no drivers specified");
        }

        // Skip drivers this order already holds (idempotency)
        List<Long> newDriverIds = driverIds.stream()
                .filter(id -> !reservationRepository
                        .existsByTravelOrderIdAndResourceTypeAndResourceIdAndReleasedAtIsNull(
                                travelOrderId, "DRIVER", id))
                .collect(Collectors.toList());

        if (newDriverIds.isEmpty()) {
            log.info("Saga: All drivers already reserved for travel order {} — idempotent return", travelOrderId);
            return;
        }

        List<Driver> drivers = driverRepository.findAllById(newDriverIds);

        // Verify all requested driver IDs were found
        if (drivers.size() != newDriverIds.size()) {
            Set<Long> foundIds = drivers.stream()
                    .map(Driver::getIdDriver)
                    .collect(Collectors.toSet());
            List<Long> missingIds = newDriverIds.stream()
                    .filter(id -> !foundIds.contains(id))
                    .collect(Collectors.toList());
            throw new DriverNotAvailableException("Drivers not found: " + missingIds);
        }

        // All-or-nothing availability check — fail fast if any are not AVAILABLE
        List<Driver> unavailable = drivers.stream()
                .filter(d -> !"AVAILABLE".equals(d.getStatus()))
                .collect(Collectors.toList());

        if (!unavailable.isEmpty()) {
            String details = unavailable.stream()
                    .map(d -> "driver " + d.getIdDriver() + " (" + d.getStatus() + ")")
                    .collect(Collectors.joining(", "));
            throw new DriverNotAvailableException("Drivers not AVAILABLE: " + details);
        }

        // Insert a reservation and recalculate status for each new driver — one transaction
        Instant now = Instant.now();
        drivers.forEach(d -> {
            reservationRepository.save(ResourceReservation.builder()
                    .travelOrderId(travelOrderId)
                    .resourceType("DRIVER")
                    .resourceId(d.getIdDriver())
                    .reservedAt(now)
                    .build());
            statusCalculationService.recalculateAndSaveDriverStatus(d.getIdDriver());
        });

        log.info("Saga: Drivers {} reserved for travel order {}", driverIds, travelOrderId);
    }

    /** Compensation — recalculates real status per driver instead of assuming AVAILABLE. */
    @Transactional
    public void releaseDrivers(List<Long> driverIds, Long travelOrderId) {
        driverIds.forEach(id -> {
            try {
                statusCalculationService.recalculateAndSaveDriverStatus(id);
            } catch (Exception e) {
                log.error("Saga: Failed to release driver {}: {}", id, e.getMessage());
            }
        });
        log.info("Saga: Drivers {} released (compensation) for travel order {}", driverIds, travelOrderId);
    }
}
