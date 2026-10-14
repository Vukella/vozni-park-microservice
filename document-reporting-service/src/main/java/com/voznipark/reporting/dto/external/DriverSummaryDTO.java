package com.voznipark.reporting.dto.external;

public record DriverSummaryDTO(
        Long idDriver,
        Integer sapNumber,
        String fullName,
        String status,
        Integer statusCode
) {}