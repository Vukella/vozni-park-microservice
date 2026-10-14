package com.voznipark.maintenance.dto;

import java.time.Instant;

public record ServiceRecordResponseDTO(
        Long id,
        Long vehicleId,
        Integer vehicleSapNumber,
        String serviceType,
        String triggerType,
        String status,
        String description,
        Long sourceEventReference,
        Instant scheduledAt,
        Instant createdAt
) {}
