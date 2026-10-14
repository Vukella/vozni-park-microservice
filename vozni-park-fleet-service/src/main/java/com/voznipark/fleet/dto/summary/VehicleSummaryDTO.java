package com.voznipark.fleet.dto.summary;

public record VehicleSummaryDTO(
        Long idVehicle,
        Integer sapNumber,
        String chassisNumber,
        String vehicleStatus,
        Integer statusCode
) {}