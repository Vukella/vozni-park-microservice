package com.voznipark.fleet.mapper;

import com.voznipark.fleet.dto.response.TrafficAccidentResponseDTO;
import com.voznipark.fleet.dto.summary.DriverSummaryDTO;
import com.voznipark.fleet.dto.summary.LocationUnitSummaryDTO;
import com.voznipark.fleet.dto.summary.VehicleSummaryDTO;
import com.voznipark.fleet.entity.Driver;
import com.voznipark.fleet.entity.LocationUnit;
import com.voznipark.fleet.entity.TrafficAccident;
import com.voznipark.fleet.entity.Vehicle;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TrafficAccidentMapper {

    public TrafficAccidentResponseDTO toResponseDTO(TrafficAccident accident) {
        return new TrafficAccidentResponseDTO(
                accident.getIdTrafficAccident(),
                toVehicleSummary(accident.getVehicle()),
                toDriverSummary(accident.getDriver()),
                toLocationSummary(accident.getLocation()),
                accident.getAccidentDate(),
                accident.getAccidentTime(),
                accident.getLocationDescription(),
                accident.getAccidentDescription(),
                accident.getDamageDescription(),
                accident.getReportedToPolice(),
                accident.getNotReportedReason(),
                accident.getEstimatedDamageValue(),
                accident.getDriverLicenseNumberSnapshot(),
                accident.getDriverLicenseExpirySnapshot(),
                accident.getStatementGivenByDriverId(),
                accident.getTravelOrderNumberSnapshot(),
                accident.getOtherParticipants(),
                accident.getDeclarantNationalId(),
                accident.getDeclarantAddress(),
                accident.getDeclarantEmail(),
                accident.getDeclarantPosition(),
                accident.getCreatedByUserId()
        );
    }

    public List<TrafficAccidentResponseDTO> toResponseDTOList(List<TrafficAccident> accidents) {
        return accidents.stream().map(this::toResponseDTO).toList();
    }

    // Helpers

    private VehicleSummaryDTO toVehicleSummary(Vehicle v) {
        if (v == null) return null;
        return new VehicleSummaryDTO(
                v.getIdVehicle(),
                v.getSapNumber(),
                v.getChassisNumber(),
                v.getVehicleStatus(),
                v.getStatusCode()
        );
    }

    private DriverSummaryDTO toDriverSummary(Driver d) {
        if (d == null) return null;
        return new DriverSummaryDTO(
                d.getIdDriver(),
                d.getSapNumber(),
                d.getFullName(),
                d.getStatus(),
                d.getStatusCode()
        );
    }

    private LocationUnitSummaryDTO toLocationSummary(LocationUnit lu) {
        if (lu == null) return null;
        return new LocationUnitSummaryDTO(
                lu.getIdLocationUnit(),
                lu.getLocationName(),
                lu.getLocationAddress()
        );
    }
}