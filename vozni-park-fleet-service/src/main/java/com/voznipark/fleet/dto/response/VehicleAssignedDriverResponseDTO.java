package com.voznipark.fleet.dto.response;

import com.voznipark.fleet.dto.summary.DriverSummaryDTO;
import com.voznipark.fleet.dto.summary.VehicleSummaryDTO;

import java.time.LocalDate;

public record VehicleAssignedDriverResponseDTO(
        Long idVehicleAssignedDriver,
        VehicleSummaryDTO vehicle,
        DriverSummaryDTO driver,
        LocalDate assignmentDate,
        String note
) {}