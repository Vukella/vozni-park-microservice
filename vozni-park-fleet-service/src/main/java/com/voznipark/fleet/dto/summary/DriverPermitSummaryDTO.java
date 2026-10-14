package com.voznipark.fleet.dto.summary;

import java.time.LocalDate;

public record DriverPermitSummaryDTO(
        Long idDriverPermit,
        LocalDate dateFrom,
        LocalDate dateTo,
        String status,
        Integer statusCode
) {}