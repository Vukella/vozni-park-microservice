package com.voznipark.fleet.dto.response;

public record DriverResponseDTO(
        Long idDriver,
        Integer sapNumber,
        String fullName,
        String phone,
        String status,
        Integer statusCode,
        Long zaposleniId,
        Boolean licenseRevoked,
        Long medicalCertificateId,
        Long driverPermitId
) {}