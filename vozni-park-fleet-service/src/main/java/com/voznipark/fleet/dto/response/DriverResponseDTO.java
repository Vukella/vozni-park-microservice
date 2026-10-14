package com.voznipark.fleet.dto.response;

public record DriverResponseDTO(
        Long idDriver,
        Integer sapNumber,
        String fullName,
        String phone,
        String status,
        Integer statusCode,
        Long employeeId,
        Boolean licenseRevoked,
        Long medicalCertificateId,
        Long driverPermitId
) {}