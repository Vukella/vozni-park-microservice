package com.voznipark.fleet.service;

import com.voznipark.fleet.dto.request.TrafficAccidentRequestDTO;
import com.voznipark.fleet.dto.response.TrafficAccidentResponseDTO;
import com.voznipark.fleet.entity.TrafficAccident;
import com.voznipark.fleet.exception.EntityNotFoundException;
import com.voznipark.fleet.kafka.event.TrafficAccidentReportedEvent;
import com.voznipark.fleet.kafka.producer.FleetEventProducer;
import com.voznipark.fleet.mapper.TrafficAccidentMapper;
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
public class TrafficAccidentService {

    private final TrafficAccidentRepository trafficAccidentRepository;
    private final VehicleRepository vehicleRepository;
    private final DriverRepository driverRepository;
    private final LocationUnitRepository locationUnitRepository;
    private final FleetEventProducer fleetEventProducer;
    private final TrafficAccidentMapper trafficAccidentMapper;

    // Queries

    public List<TrafficAccidentResponseDTO> getAccidentsByVehicle(Long vehicleId) {
        return trafficAccidentMapper.toResponseDTOList(
                trafficAccidentRepository.findByVehicle_IdVehicleOrderByAccidentDateDesc(vehicleId));
    }

    public List<TrafficAccidentResponseDTO> getAccidentsByDriver(Long driverId) {
        return trafficAccidentMapper.toResponseDTOList(
                trafficAccidentRepository.findByDriver_IdDriverOrderByAccidentDateDesc(driverId));
    }

    public TrafficAccidentResponseDTO getAccidentById(Long accidentId) {
        return trafficAccidentMapper.toResponseDTO(
                trafficAccidentRepository.findById(accidentId)
                        .orElseThrow(() -> new EntityNotFoundException("TrafficAccident", accidentId)));
    }

    // Mutations

    /**
     * Records a traffic accident. Snapshot fields capture the state of driver
     * license and travel order data at the moment of recording — they are
     * immutable after creation and survive future document renewals or order closures.
     */
    @Transactional
    public TrafficAccidentResponseDTO reportAccident(TrafficAccidentRequestDTO dto) {
        TrafficAccident accident = new TrafficAccident();

        accident.setVehicle(vehicleRepository.findById(dto.vehicleId())
                .orElseThrow(() -> new EntityNotFoundException("Vehicle", dto.vehicleId())));

        accident.setAccidentDate(dto.accidentDate() != null ? dto.accidentDate() : LocalDate.now());
        accident.setAccidentTime(dto.accidentTime());
        accident.setLocationDescription(dto.locationDescription());
        accident.setAccidentDescription(dto.accidentDescription());
        accident.setDamageDescription(dto.damageDescription());
        accident.setReportedToPolice(dto.reportedToPolice() != null ? dto.reportedToPolice() : Boolean.FALSE);
        accident.setNotReportedReason(dto.notReportedReason());
        accident.setEstimatedDamageValue(dto.estimatedDamageValue());
        accident.setDriverLicenseNumberSnapshot(dto.driverLicenseNumberSnapshot());
        accident.setDriverLicenseExpirySnapshot(dto.driverLicenseExpirySnapshot());
        accident.setStatementGivenByDriverId(dto.statementGivenByDriverId());
        accident.setTravelOrderNumberSnapshot(dto.travelOrderNumberSnapshot());
        accident.setOtherParticipants(dto.otherParticipants());
        accident.setDeclarantNationalId(dto.declarantNationalId());
        accident.setDeclarantAddress(dto.declarantAddress());
        accident.setDeclarantEmail(dto.declarantEmail());
        accident.setDeclarantPosition(dto.declarantPosition());
        accident.setSignature(dto.signature());
        accident.setCreatedByUserId(dto.createdByUserId());

        if (dto.driverId() != null) {
            accident.setDriver(driverRepository.getReferenceById(dto.driverId()));
        }
        if (dto.locationId() != null) {
            accident.setLocation(locationUnitRepository.getReferenceById(dto.locationId()));
        }

        TrafficAccident saved = trafficAccidentRepository.save(accident);

        fleetEventProducer.publishTrafficAccidentReported(
                TrafficAccidentReportedEvent.builder()
                        .accidentId(saved.getIdTrafficAccident())
                        .vehicleId(dto.vehicleId())
                        .vehicleSapNumber(saved.getVehicle().getSapNumber())
                        .driverId(dto.driverId())
                        .travelOrderNumberSnapshot(dto.travelOrderNumberSnapshot())
                        .driverLicenseNumberSnapshot(dto.driverLicenseNumberSnapshot())
                        .locationId(dto.locationId())
                        .reportedAt(Instant.now())
                        .build());

        return trafficAccidentMapper.toResponseDTO(
                trafficAccidentRepository.findById(saved.getIdTrafficAccident()).orElseThrow());
    }
}