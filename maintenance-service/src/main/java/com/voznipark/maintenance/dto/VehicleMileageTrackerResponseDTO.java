package com.voznipark.maintenance.dto;

import java.time.Instant;

public record VehicleMileageTrackerResponseDTO(
        Long vehicleId,
        Integer vehicleSapNumber,
        Integer totalMileageKm,
        Integer kmSinceLastMinorService,
        Integer kmSinceLastMajorService,
        Long lastTravelOrderId,
        Instant lastUpdated
) {}
