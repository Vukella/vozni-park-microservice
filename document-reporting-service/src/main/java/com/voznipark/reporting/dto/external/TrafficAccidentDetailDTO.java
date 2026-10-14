package com.voznipark.reporting.dto.external;

import java.math.BigDecimal;
import java.time.LocalDate;


public record TrafficAccidentDetailDTO(
        Long idTrafficAccident,
        VehicleSummaryDTO vehicle,
        DriverSummaryDTO driver,
        LocationUnitSummaryDTO location,
        LocalDate accidentDate,
        String accidentTime,
        String locationDescription,
        String accidentDescription,
        String damageDescription,
        Boolean reportedToPolice,
        String notReportedReason,
        BigDecimal estimatedDamageValue,
        String driverLicenseNumberSnapshot,
        LocalDate driverLicenseExpirySnapshot,
        Long statementGivenByDriverId,
        String travelOrderNumberSnapshot,
        String otherParticipants,
        String declarantNationalId,
        String declarantAddress,
        String declarantEmail,
        String declarantPosition,
        Long createdByUserId
) {}
