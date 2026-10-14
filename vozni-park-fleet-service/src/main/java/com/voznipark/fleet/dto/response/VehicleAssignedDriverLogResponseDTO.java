package com.voznipark.fleet.dto.response;

import com.voznipark.fleet.dto.summary.VehicleSummaryDTO;

import java.time.LocalDate;

public record VehicleAssignedDriverLogResponseDTO(
        Long idVehicleAssignedDriverLog,
        VehicleSummaryDTO vehicle,
        Long previousDriverId,
        Long newDriverId,
        LocalDate changeDate,
        Long changedByUserId
) {}