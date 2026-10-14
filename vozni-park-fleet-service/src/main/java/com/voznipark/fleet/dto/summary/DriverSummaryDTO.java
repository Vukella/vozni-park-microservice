package com.voznipark.fleet.dto.summary;

public record DriverSummaryDTO(
        Long idDriver,
        Integer sapNumber,
        String fullName,
        String status,
        Integer statusCode
) {}