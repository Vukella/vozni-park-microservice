package com.voznipark.fleet.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record DriverRequestDTO(

        @NotNull(message = "SAP number is required")
        Integer sapNumber,

        @NotBlank(message = "Full name is required")
        @Size(max = 60, message = "Full name must not exceed 60 characters")
        String fullName,

        @Size(max = 20, message = "Phone must not exceed 20 characters")
        String phone,

        Long employeeId,

        @NotNull(message = "License revoked flag is required")
        Boolean licenseRevoked,

        Long medicalCertificateId,
        Long driverPermitId
) {}