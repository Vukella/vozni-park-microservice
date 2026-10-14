package com.voznipark.fleet.dto.request;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record VehicleFaultRequestDTO(

        @NotNull(message = "Vehicle ID is required")
        Long vehicleId,

        @NotNull(message = "Reporting driver ID is required")
        Long reportedByDriverId,

        Long locationId,
        Long handoverDriverId,

        LocalDate reportDate,

        Integer mileageAtFault,

        String faultDescription,

        Boolean vehicleInoperable,
        Boolean towRequired,

        /** MINOR or MAJOR for scheduled services, null for ordinary faults. */
        String serviceType,

        /** Used only when serviceType is MINOR or MAJOR. */
        LocalDate scheduledDate,

        LocalDate handoverDate,

        String signature,

        Long createdByUserId
) {}