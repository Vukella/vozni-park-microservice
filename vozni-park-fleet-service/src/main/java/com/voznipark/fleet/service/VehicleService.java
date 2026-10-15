package com.voznipark.fleet.service;

import com.voznipark.fleet.dto.request.VehicleRequestDTO;
import com.voznipark.fleet.dto.response.VehicleResponseDTO;
import com.voznipark.fleet.entity.Vehicle;
import com.voznipark.fleet.exception.BusinessRuleViolationException;
import com.voznipark.fleet.exception.DuplicateEntityException;
import com.voznipark.fleet.exception.EntityNotFoundException;
import com.voznipark.fleet.exception.VehicleNotAvailableException;
import com.voznipark.fleet.kafka.event.VehicleDecommissionedEvent;
import com.voznipark.fleet.kafka.event.VehicleStatusChangedEvent;
import com.voznipark.fleet.kafka.producer.FleetEventProducer;
import com.voznipark.fleet.mapper.VehicleMapper;
import com.voznipark.fleet.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.voznipark.fleet.entity.ResourceReservation;
import com.voznipark.fleet.repository.ResourceReservationRepository;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final RegistrationRepository registrationRepository;
    private final FirstAidKitRepository firstAidKitRepository;
    private final TachographDeviceRepository tachographDeviceRepository;
    private final CascoInsuranceRepository cascoInsuranceRepository;
    private final FireExtinguisherRepository fireExtinguisherRepository;
    private final FuelTypeRepository fuelTypeRepository;
    private final VehicleModelRepository vehicleModelRepository;
    private final VehicleTypeRepository vehicleTypeRepository;
    private final VehicleSubtypeRepository vehicleSubtypeRepository;
    private final NisCardRepository nisCardRepository;
    private final TireMarkingRepository tireMarkingRepository;
    private final VehicleMapper vehicleMapper;
    private final StatusCalculationService statusCalculationService;
    private final FleetEventProducer fleetEventProducer;
    private final ResourceReservationRepository reservationRepository;

    public List<VehicleResponseDTO> getAllVehicles() {
        return vehicleRepository.findAllWithRelations().stream()
                .map(vehicleMapper::toResponseDTO)
                .toList();
    }

    public Optional<VehicleResponseDTO> getVehicleById(Long vehicleId) {
        return vehicleRepository.findByIdWithRelations(vehicleId)
                .map(vehicleMapper::toResponseDTO);
    }

    // status is never taken from the request — recalculateStatus() sets it after save
    @Transactional
    public VehicleResponseDTO createVehicle(VehicleRequestDTO dto) {
        validateSapNumberUnique(dto.getSapNumber(), null);

        Vehicle vehicle = vehicleMapper.toEntity(dto);
        applyRelations(vehicle, dto);

        Vehicle saved = vehicleRepository.save(vehicle);
        recalculateStatus(saved.getIdVehicle());

        Vehicle refreshed = vehicleRepository.findByIdWithRelations(saved.getIdVehicle())
                .orElseThrow(() -> new EntityNotFoundException("Vehicle", saved.getIdVehicle()));
        return vehicleMapper.toResponseDTO(refreshed);
    }

    // PUT semantics: null relation ID clears that relation
    @Transactional
    public VehicleResponseDTO updateVehicle(Long vehicleId, VehicleRequestDTO dto) {
        Vehicle existing = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new EntityNotFoundException("Vehicle", vehicleId));

        validateSapNumberUnique(dto.getSapNumber(), vehicleId);

        vehicleMapper.updateEntity(existing, dto);
        applyRelations(existing, dto);

        vehicleRepository.save(existing);
        recalculateStatus(vehicleId);

        Vehicle refreshed = vehicleRepository.findByIdWithRelations(vehicleId)
                .orElseThrow(() -> new EntityNotFoundException("Vehicle", vehicleId));
        return vehicleMapper.toResponseDTO(refreshed);
    }

    private void validateSapNumberUnique(Integer sapNumber, Long excludeVehicleId) {
        vehicleRepository.findBySapNumber(sapNumber).ifPresent(existing -> {
            if (excludeVehicleId == null || !existing.getIdVehicle().equals(excludeVehicleId)) {
                throw new DuplicateEntityException(
                        "Vehicle with SAP number " + sapNumber + " already exists");
            }
        });
    }

    // resolves all 11 FK relations; null id = clear the relation
    private void applyRelations(Vehicle vehicle, VehicleRequestDTO dto) {
        vehicle.setRegistration(resolveOptional(dto.getRegistrationId(), registrationRepository, "Registration"));
        vehicle.setFirstAidKit(resolveOptional(dto.getFirstAidKitId(), firstAidKitRepository, "FirstAidKit"));
        vehicle.setTachographDevice(resolveOptional(dto.getTachographDeviceId(), tachographDeviceRepository, "TachographDevice"));
        vehicle.setCascoInsurance(resolveOptional(dto.getCascoInsuranceId(), cascoInsuranceRepository, "CascoInsurance"));
        vehicle.setFireExtinguisher(resolveOptional(dto.getFireExtinguisherId(), fireExtinguisherRepository, "FireExtinguisher"));
        vehicle.setFuelType(resolveOptional(dto.getFuelTypeId(), fuelTypeRepository, "FuelType"));
        vehicle.setVehicleModel(resolveOptional(dto.getVehicleModelId(), vehicleModelRepository, "VehicleModel"));
        vehicle.setVehicleType(resolveOptional(dto.getVehicleTypeId(), vehicleTypeRepository, "VehicleType"));
        vehicle.setVehicleSubtype(resolveOptional(dto.getVehicleSubtypeId(), vehicleSubtypeRepository, "VehicleSubtype"));
        vehicle.setNisCard(resolveOptional(dto.getNisCardId(), nisCardRepository, "NisCard"));
        vehicle.setTireMarking(resolveOptional(dto.getTireMarkingId(), tireMarkingRepository, "TireMarking"));
    }

    private <T> T resolveOptional(Long id, JpaRepository<T, Long> repository, String entityName) {
        if (id == null) {
            return null;
        }
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(entityName, id));
    }

    // permanent — StatusCalculationService skips decommissioned vehicles
    @Transactional
    public void decommissionVehicle(Long vehicleId) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new EntityNotFoundException("Vehicle", vehicleId));

        if (Boolean.TRUE.equals(vehicle.getDecommissioned())) {
            throw new BusinessRuleViolationException("ALREADY_DECOMMISSIONED",
                    "Vehicle " + vehicleId + " is already decommissioned");
        }

        LocalDate decommissionDate = LocalDate.now();
        vehicle.setDecommissioned(true);
        vehicle.setDecommissionDate(decommissionDate);
        vehicleRepository.save(vehicle);

        fleetEventProducer.publishVehicleDecommissioned(
                VehicleDecommissionedEvent.builder()
                        .vehicleId(vehicleId)
                        .vehicleSapNumber(vehicle.getSapNumber())
                        .decommissionDate(decommissionDate)
                        .locationId(null)
                        .occurredAt(Instant.now())
                        .build());
    }

    // publishes VehicleStatusChanged only if the status actually changed
    @Transactional
    public void recalculateStatus(Long vehicleId) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new EntityNotFoundException("Vehicle", vehicleId));
        String previousStatus = vehicle.getVehicleStatus();

        statusCalculationService.recalculateAndSaveVehicleStatus(vehicleId);

        String newStatus = vehicleRepository.findById(vehicleId)
                .map(Vehicle::getVehicleStatus).orElse(previousStatus);

        if (!newStatus.equals(previousStatus)) {
            fleetEventProducer.publishVehicleStatusChanged(
                    VehicleStatusChangedEvent.builder()
                            .vehicleId(vehicleId)
                            .sapNumber(vehicle.getSapNumber())
                            .oldStatus(previousStatus)
                            .newStatus(newStatus)
                            .locationId(null)
                            .occurredAt(Instant.now())
                            .build());
        }
    }

    // ── Saga reservation methods ───────────────────────────────────────────────

    /** Saga step 1 — sets vehicle ON_DUTY if AVAILABLE, returns its SAP number. */
    @Transactional
    public Integer reserveVehicle(Long vehicleId, Long travelOrderId) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new EntityNotFoundException("Vehicle", vehicleId));

        // Idempotency: already reserved by this order — return SAP without failing
        if (reservationRepository.existsByTravelOrderIdAndResourceTypeAndResourceIdAndReleasedAtIsNull(
                travelOrderId, "VEHICLE", vehicleId)) {
            log.info("Saga: Vehicle {} already reserved for travel order {} — idempotent return",
                    vehicleId, travelOrderId);
            return vehicle.getSapNumber();
        }

        if (Boolean.TRUE.equals(vehicle.getDecommissioned())) {
            throw new VehicleNotAvailableException(
                    "Vehicle " + vehicleId + " is decommissioned and cannot be reserved");
        }

        if (!"AVAILABLE".equals(vehicle.getVehicleStatus())) {
            throw new VehicleNotAvailableException(
                    "Vehicle " + vehicleId + " is not AVAILABLE. Current status: " + vehicle.getVehicleStatus());
        }

        reservationRepository.save(ResourceReservation.builder()
                .travelOrderId(travelOrderId)
                .resourceType("VEHICLE")
                .resourceId(vehicleId)
                .reservedAt(Instant.now())
                .build());

        statusCalculationService.recalculateAndSaveVehicleStatus(vehicleId);

        log.info("Saga: Vehicle {} reserved for travel order {}", vehicleId, travelOrderId);
        return vehicle.getSapNumber();
    }

    /** Compensation step — recalculates real status rather than hardcoding AVAILABLE. */
    @Transactional
    public void releaseVehicle(Long vehicleId, Long travelOrderId) {
        vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new EntityNotFoundException("Vehicle", vehicleId));

        reservationRepository
                .findByTravelOrderIdAndResourceTypeAndReleasedAtIsNull(travelOrderId, "VEHICLE")
                .forEach(r -> {
                    r.setReleasedAt(Instant.now());
                    r.setReleaseReason("COMPENSATION");
                    reservationRepository.save(r);
                });

        statusCalculationService.recalculateAndSaveVehicleStatus(vehicleId);

        log.info("Saga: Vehicle {} released (compensation) for travel order {}", vehicleId, travelOrderId);
    }
}
