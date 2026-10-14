package com.voznipark.fleet.dto.request;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TrafficAccidentRequestDTO(

        @NotNull(message = "Vehicle ID is required")
        Long vehicleId,

        LocalDate accidentDate,
        String accidentTime,
        String locationDescription,
        String accidentDescription,
        String damageDescription,
        Boolean reportedToPolice,
        String notReportedReason,
        BigDecimal estimatedDamageValue,

        Long driverId,
        String driverLicenseNumberSnapshot,
        LocalDate driverLicenseExpirySnapshot,
        Long statementGivenByDriverId,

        Long locationId,
        String travelOrderNumberSnapshot,
        String otherParticipants,

        String declarantNationalId,
        String declarantAddress,
        String declarantEmail,
        String declarantPosition,

        String signature,
        Long createdByUserId
) {}