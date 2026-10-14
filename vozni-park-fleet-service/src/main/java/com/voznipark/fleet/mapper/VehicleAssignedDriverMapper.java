package com.voznipark.fleet.mapper;

import com.voznipark.fleet.dto.response.VehicleAssignedDriverLogResponseDTO;
import com.voznipark.fleet.dto.response.VehicleAssignedDriverResponseDTO;
import com.voznipark.fleet.dto.summary.DriverSummaryDTO;
import com.voznipark.fleet.dto.summary.VehicleSummaryDTO;
import com.voznipark.fleet.entity.Driver;
import com.voznipark.fleet.entity.Vehicle;
import com.voznipark.fleet.entity.VehicleAssignedDriver;
import com.voznipark.fleet.entity.VehicleAssignedDriverLog;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class VehicleAssignedDriverMapper {

    public VehicleAssignedDriverResponseDTO toResponseDTO(VehicleAssignedDriver assignment) {
        return new VehicleAssignedDriverResponseDTO(
                assignment.getIdVehicleAssignedDriver(),
                toVehicleSummary(assignment.getVehicle()),
                toDriverSummary(assignment.getDriver()),
                assignment.getAssignmentDate(),
                assignment.getNote()
        );
    }

    public VehicleAssignedDriverLogResponseDTO toLogResponseDTO(VehicleAssignedDriverLog log) {
        return new VehicleAssignedDriverLogResponseDTO(
                log.getIdVehicleAssignedDriverLog(),
                toVehicleSummary(log.getVehicle()),
                log.getPreviousDriverId(),
                log.getNewDriverId(),
                log.getChangeDate(),
                log.getChangedByUserId()
        );
    }

    public List<VehicleAssignedDriverLogResponseDTO> toLogResponseDTOList(
            List<VehicleAssignedDriverLog> logs) {
        return logs.stream().map(this::toLogResponseDTO).toList();
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
}