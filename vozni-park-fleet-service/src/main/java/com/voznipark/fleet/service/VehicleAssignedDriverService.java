package com.voznipark.fleet.service;

import com.voznipark.fleet.dto.request.VehicleAssignedDriverRequestDTO;
import com.voznipark.fleet.dto.response.VehicleAssignedDriverLogResponseDTO;
import com.voznipark.fleet.dto.response.VehicleAssignedDriverResponseDTO;
import com.voznipark.fleet.entity.*;
import com.voznipark.fleet.exception.EntityNotFoundException;
import com.voznipark.fleet.kafka.event.VehicleAssignedDriverChangedEvent;
import com.voznipark.fleet.kafka.producer.FleetEventProducer;
import com.voznipark.fleet.mapper.VehicleAssignedDriverMapper;
import com.voznipark.fleet.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VehicleAssignedDriverService {

    private final VehicleAssignedDriverRepository assignedDriverRepository;
    private final VehicleAssignedDriverLogRepository assignedDriverLogRepository;
    private final VehicleRepository vehicleRepository;
    private final DriverRepository driverRepository;
    private final FleetEventProducer fleetEventProducer;
    private final VehicleAssignedDriverMapper assignedDriverMapper;

    // Queries

    public Optional<VehicleAssignedDriverResponseDTO> getCurrentAssignment(Long vehicleId) {
        return assignedDriverRepository.findByVehicle_IdVehicle(vehicleId)
                .map(assignedDriverMapper::toResponseDTO);
    }

    public List<VehicleAssignedDriverLogResponseDTO> getAssignmentHistory(Long vehicleId) {
        return assignedDriverMapper.toLogResponseDTOList(
                assignedDriverLogRepository.findByVehicle_IdVehicleOrderByChangeDateDesc(vehicleId));
    }

    // Mutations

    /**
     * Assigns a driver to a vehicle. Handles both first-time assignment and reassignment.
     * flush() between delete and insert is required to avoid unique constraint violation
     * on vehicle_id before the new record is saved.
     */
    @Transactional
    public VehicleAssignedDriverResponseDTO assignDriver(VehicleAssignedDriverRequestDTO dto) {
        Vehicle vehicle = vehicleRepository.findById(dto.vehicleId())
                .orElseThrow(() -> new EntityNotFoundException("Vehicle", dto.vehicleId()));
        Driver driver = driverRepository.findById(dto.driverId())
                .orElseThrow(() -> new EntityNotFoundException("Driver", dto.driverId()));

        Long previousDriverId = assignedDriverRepository
                .findByVehicle_IdVehicle(dto.vehicleId())
                .map(a -> a.getDriver().getIdDriver())
                .orElse(null);

        assignedDriverRepository.findByVehicle_IdVehicle(dto.vehicleId())
                .ifPresent(assignedDriverRepository::delete);
        assignedDriverRepository.flush();

        VehicleAssignedDriver assignment = new VehicleAssignedDriver();
        assignment.setVehicle(vehicle);
        assignment.setDriver(driver);
        assignment.setAssignmentDate(LocalDate.now());
        assignment.setNote(dto.note());
        VehicleAssignedDriver saved = assignedDriverRepository.save(assignment);

        VehicleAssignedDriverLog log = new VehicleAssignedDriverLog();
        log.setVehicle(vehicle);
        log.setPreviousDriverId(previousDriverId);
        log.setNewDriverId(dto.driverId());
        log.setChangeDate(LocalDate.now());
        log.setChangedByUserId(dto.changedByUserId());
        assignedDriverLogRepository.save(log);

        fleetEventProducer.publishVehicleAssignedDriverChanged(
                VehicleAssignedDriverChangedEvent.builder()
                        .vehicleId(dto.vehicleId())
                        .vehicleSapNumber(vehicle.getSapNumber())
                        .oldDriverId(previousDriverId)
                        .newDriverId(dto.driverId())
                        .locationId(null)
                        .assignedAt(Instant.now())
                        .build());

        return assignedDriverMapper.toResponseDTO(saved);
    }
}