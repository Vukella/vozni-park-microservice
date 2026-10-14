package com.voznipark.fleet.dto.response;

import com.voznipark.fleet.dto.summary.DriverSummaryDTO;
import com.voznipark.fleet.dto.summary.LocationUnitSummaryDTO;
import com.voznipark.fleet.dto.summary.VehicleSummaryDTO;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TrafficAccidentResponseDTO(
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
        // signature intentionally omitted — Base64 PNG, too large for list responses
) {}