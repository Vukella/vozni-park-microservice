package com.voznipark.fleet.dto.summary;

public record LocationUnitSummaryDTO(
        Long idLocationUnit,
        String locationName,
        String locationAddress
) {}