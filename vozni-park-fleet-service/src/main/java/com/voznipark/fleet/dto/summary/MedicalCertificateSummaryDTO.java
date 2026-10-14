package com.voznipark.fleet.dto.summary;

import java.time.LocalDate;

public record MedicalCertificateSummaryDTO(
        Long idMedicalCertificate,
        LocalDate dateFrom,
        LocalDate dateTo,
        String status,
        Integer statusCode
) {}