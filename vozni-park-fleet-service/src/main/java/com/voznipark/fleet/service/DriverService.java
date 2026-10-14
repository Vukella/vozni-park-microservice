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

        List<Driver> drivers = driverRepository.findAllById(driverIds);

        // Verify all requested driver IDs were found
        if (drivers.size() != driverIds.size()) {
            Set<Long> foundIds = drivers.stream()
                    .map(Driver::getIdDriver)
                    .collect(Collectors.toSet());
            List<Long> missingIds = driverIds.stream()
                    .filter(id -> !foundIds.contains(id))
                    .collect(Collectors.toList());
            throw new DriverNotAvailableException("Drivers not found: " + missingIds);
        }

        // Verify ALL drivers are AVAILABLE — fail fast if any are not
        List<Driver> unavailable = drivers.stream()
                .filter(d -> !"AVAILABLE".equals(d.getStatus()))
                .collect(Collectors.toList());

        if (!unavailable.isEmpty()) {
            String details = unavailable.stream()
                    .map(d -> "driver " + d.getIdDriver() + " (" + d.getStatus() + ")")
                    .collect(Collectors.joining(", "));
            throw new DriverNotAvailableException("Drivers not AVAILABLE: " + details);
        }

        // ON_DUTY = DriverStatus enum value persisted via .name() by StatusCalculationService
        drivers.forEach(d -> d.setStatus("ON_DUTY"));
        driverRepository.saveAll(drivers);

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
