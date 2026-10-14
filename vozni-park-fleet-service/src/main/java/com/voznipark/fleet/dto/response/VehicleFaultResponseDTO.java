package com.voznipark.fleet.dto.response;

import com.voznipark.fleet.dto.summary.DriverSummaryDTO;
import com.voznipark.fleet.dto.summary.LocationUnitSummaryDTO;
import com.voznipark.fleet.dto.summary.VehicleSummaryDTO;

import java.time.LocalDate;

public record VehicleFaultResponseDTO(
        Long idVehicleFault,
        VehicleSummaryDTO vehicle,
        DriverSummaryDTO reportedByDriver,
        DriverSummaryDTO handoverDriver,
        LocationUnitSummaryDTO location,
        LocalDate reportDate,
        LocalDate resolutionDate,
        LocalDate handoverDate,
        Integer mileageAtFault,
        String faultDescription,
        Boolean vehicleInoperable,
        Boolean towRequired,
        String serviceType,
        Long createdByUserId
) {}