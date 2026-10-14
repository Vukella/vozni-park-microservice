package com.voznipark.travelorder.mapper;

import com.voznipark.travelorder.dto.request.TravelOrderRequestDTO;
import com.voznipark.travelorder.dto.response.TravelOrderResponseDTO;
import com.voznipark.travelorder.entity.TravelOrder;
import com.voznipark.travelorder.entity.TravelOrderDriver;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class TravelOrderMapper {

    public TravelOrder toEntity(TravelOrderRequestDTO dto) {
        TravelOrder entity = new TravelOrder();
        entity.setVehicleId(dto.getVehicleId());
        entity.setDateFrom(dto.getDateFrom());
        entity.setDateTo(dto.getDateTo());
        entity.setWorkOrderNumber(dto.getWorkOrderNumber());
        entity.setLocationId(dto.getLocationId());
        entity.setCreatedByUserId(dto.getCreatedByUserId());
        entity.setStartingMileage(dto.getStartingMileage());
        return entity;
    }

    public TravelOrderResponseDTO toResponseDTO(TravelOrder entity) {
        List<TravelOrderResponseDTO.DriverAssignmentDTO> driverDTOs = entity.getDrivers() == null
                ? List.of()
                : entity.getDrivers().stream()
                        .map(this::toDriverAssignmentDTO)
                        .collect(Collectors.toList());

        return TravelOrderResponseDTO.builder()
                .idTravelOrder(entity.getIdTravelOrder())
                .vehicleId(entity.getVehicleId())
                .dateFrom(entity.getDateFrom())
                .dateTo(entity.getDateTo())
                .workOrderNumber(entity.getWorkOrderNumber())
                .travelOrderNumber(entity.getTravelOrderNumber())
                .locationId(entity.getLocationId())
                .startingMileage(entity.getStartingMileage())
                .endingMileage(entity.getEndingMileage())
                .status(entity.getStatus())
                .sagaStatus(entity.getSagaStatus())
                .failureReason(entity.getFailureReason())
                .createdByUserId(entity.getCreatedByUserId())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .drivers(driverDTOs)
                .build();
    }

    private TravelOrderResponseDTO.DriverAssignmentDTO toDriverAssignmentDTO(TravelOrderDriver d) {
        return TravelOrderResponseDTO.DriverAssignmentDTO.builder()
                .driverId(d.getDriverId())
                .isPrimary(d.getIsPrimary())
                .build();
    }
}
