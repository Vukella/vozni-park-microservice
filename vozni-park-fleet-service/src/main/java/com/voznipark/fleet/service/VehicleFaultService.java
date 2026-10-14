package com.voznipark.fleet.service;

import com.voznipark.fleet.dto.request.VehicleFaultRequestDTO;
import com.voznipark.fleet.dto.response.VehicleFaultResponseDTO;
import com.voznipark.fleet.entity.*;
import com.voznipark.fleet.exception.EntityNotFoundException;
import com.voznipark.fleet.kafka.event.*;
import com.voznipark.fleet.kafka.producer.FleetEventProducer;
import com.voznipark.fleet.mapper.VehicleFaultMapper;
import com.voznipark.fleet.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VehicleFaultService {

    private final VehicleFaultRepository vehicleFaultRepository;
    private final VehicleRepository vehicleRepository;
    private final DriverRepository driverRepository;
    private final LocationUnitRepository locationUnitRepository;
    private final StatusCalculationService statusCalculationService;
    private final FleetEventProducer fleetEventProducer;
    private final VehicleFaultMapper vehicleFaultMapper;

    // Queries

    public List<VehicleFaultResponseDTO> getFaultsByVehicle(Long vehicleId) {
        return vehicleFaultMapper.toResponseDTOList(
                vehicleFaultRepository.findByVehicle_IdVehicleOrderByReportDateDesc(vehicleId));
    }

    public VehicleFaultResponseDTO getFaultById(Long faultId) {
        return vehicleFaultMapper.toResponseDTO(
                vehicleFaultRepository.findById(faultId)
                        .orElseThrow(() -> new EntityNotFoundException("VehicleFault", faultId)));
    }

    // Mutations

    @Transactional
    public VehicleFaultResponseDTO reportFault(VehicleFaultRequestDTO dto) {
        Vehicle vehicle = vehicleRepository.findById(dto.vehicleId())
                .orElseThrow(() -> new EntityNotFoundException("Vehicle", dto.vehicleId()));
        Driver reportedByDriver = driverRepository.findById(dto.reportedByDriverId())
                .orElseThrow(() -> new EntityNotFoundException("Driver", dto.reportedByDriverId()));

        String previousStatus = vehicle.getVehicleStatus();

        VehicleFault fault = new VehicleFault();
        fault.setVehicle(vehicle);
        fault.setReportedByDriver(reportedByDriver);
        fault.setReportDate(dto.reportDate() != null ? dto.reportDate() : LocalDate.now());
        fault.setMileageAtFault(dto.mileageAtFault());
        fault.setFaultDescription(dto.faultDescription());
        fault.setVehicleInoperable(dto.vehicleInoperable() != null ? dto.vehicleInoperable() : Boolean.FALSE);
        fault.setTowRequired(dto.towRequired() != null ? dto.towRequired() : Boolean.FALSE);
        fault.setServiceType(dto.serviceType());
        fault.setHandoverDate(dto.handoverDate());
        fault.setSignature(dto.signature());
        fault.setCreatedByUserId(dto.createdByUserId());

        if (dto.locationId() != null) {
            fault.setLocation(locationUnitRepository.getReferenceById(dto.locationId()));
        }
        if (dto.handoverDriverId() != null) {
            fault.setHandoverDriver(driverRepository.getReferenceById(dto.handoverDriverId()));
        }

        VehicleFault saved = vehicleFaultRepository.save(fault);
        Long faultLocationId = saved.getLocation() != null
                ? saved.getLocation().getIdLocationUnit() : null;

        if (Boolean.TRUE.equals(saved.getVehicleInoperable())) {
            statusCalculationService.recalculateAndSaveVehicleStatus(dto.vehicleId());
            String newStatus = vehicleRepository.findById(dto.vehicleId())
                    .map(Vehicle::getVehicleStatus).orElse(previousStatus);
            if (!newStatus.equals(previousStatus)) {
                fleetEventProducer.publishVehicleStatusChanged(
                        VehicleStatusChangedEvent.builder()
                                .vehicleId(dto.vehicleId())
                                .sapNumber(vehicle.getSapNumber())
                                .oldStatus(previousStatus)
                                .newStatus(newStatus)
                                .locationId(faultLocationId)
                                .occurredAt(Instant.now())
                                .build());
            }
        }

        fleetEventProducer.publishVehicleFaultReported(
                VehicleFaultReportedEvent.builder()
                        .faultId(saved.getIdVehicleFault())
                        .vehicleId(dto.vehicleId())
                        .vehicleSapNumber(vehicle.getSapNumber())
                        .description(dto.faultDescription())
                        .vehicleInoperable(saved.getVehicleInoperable())
                        .locationId(faultLocationId)
                        .occurredAt(Instant.now())
                        .build());

        if (dto.serviceType() != null
                && (dto.serviceType().equals("MINOR") || dto.serviceType().equals("MAJOR"))) {
            fleetEventProducer.publishVehicleServiceScheduled(
                    VehicleServiceScheduledEvent.builder()
                            .vehicleId(dto.vehicleId())
                            .vehicleSapNumber(vehicle.getSapNumber())
                            .serviceType(dto.serviceType())
                            .scheduledDate(dto.scheduledDate() != null
                                    ? dto.scheduledDate() : saved.getReportDate())
                            .locationId(faultLocationId)
                            .occurredAt(Instant.now())
                            .build());
        }

        return vehicleFaultMapper.toResponseDTO(
                vehicleFaultRepository.findById(saved.getIdVehicleFault()).orElseThrow());
    }

    @Transactional
    public VehicleFaultResponseDTO resolveFault(Long faultId, LocalDate resolutionDate) {
        VehicleFault fault = vehicleFaultRepository.findById(faultId)
                .orElseThrow(() -> new EntityNotFoundException("VehicleFault", faultId));

        Vehicle vehicle = fault.getVehicle();
        Long vehicleId = vehicle.getIdVehicle();
        String previousStatus = vehicle.getVehicleStatus();
        Long faultLocationId = fault.getLocation() != null
                ? fault.getLocation().getIdLocationUnit() : null;

        fault.setResolutionDate(resolutionDate != null ? resolutionDate : LocalDate.now());
        vehicleFaultRepository.save(fault);

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
                            .locationId(faultLocationId)
                            .occurredAt(Instant.now())
                            .build());
        }

        fleetEventProducer.publishVehicleFaultResolved(
                VehicleFaultResolvedEvent.builder()
                        .faultId(faultId)
                        .vehicleId(vehicleId)
                        .vehicleSapNumber(vehicle.getSapNumber())
                        .locationId(faultLocationId)
                        .resolvedAt(Instant.now())
                        .build());

        return vehicleFaultMapper.toResponseDTO(
                vehicleFaultRepository.findById(faultId).orElseThrow());
    }
}