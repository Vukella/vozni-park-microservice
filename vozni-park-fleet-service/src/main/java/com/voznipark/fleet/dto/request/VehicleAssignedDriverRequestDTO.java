package com.voznipark.fleet.dto.request;

import jakarta.validation.constraints.NotNull;

public record VehicleAssignedDriverRequestDTO(

        @NotNull(message = "Vehicle ID is required")
        Long vehicleId,

        @NotNull(message = "Driver ID is required")
        Long driverId,

        String note,

        Long changedByUserId
) {}