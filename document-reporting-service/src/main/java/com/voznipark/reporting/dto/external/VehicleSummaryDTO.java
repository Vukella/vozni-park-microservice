package com.voznipark.reporting.dto.external;

public record VehicleSummaryDTO(
        Long idVehicle,
        Integer sapNumber,
        String chassisNumber,
        String vehicleStatus,
        Integer statusCode
) {}