package com.voznipark.reporting.dto.external;

public record LocationUnitSummaryDTO(
        Long idLocationUnit,
        String locationName,
        String locationAddress
) {}