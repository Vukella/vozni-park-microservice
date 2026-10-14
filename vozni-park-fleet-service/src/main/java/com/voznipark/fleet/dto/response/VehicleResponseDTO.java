package com.voznipark.fleet.dto.response;

import com.voznipark.fleet.dto.summary.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleResponseDTO {

    private Long idVehicle;
    private Integer sapNumber;
    private String chassisNumber;
    private Integer engineNumber;
    private Integer tagSerialNumber;
    private Integer yearOfManufacture;
    private BigDecimal engineDisplacement;
    private Integer power;
    private String vehicleStatus;
    private Integer statusCode;

    private BigDecimal mass;
    private String color;
    private Integer loadCapacity;
    private Integer totalMass;
    private Integer numberOfSeats;
    private LocalDate firstRegistrationDate;
    private Boolean operationalLeasing;
    private Boolean decommissioned;
    private LocalDate decommissionDate;
    private String note;

    private RegistrationSummaryDTO registration;
    private FirstAidKitSummaryDTO firstAidKit;
    private TachographDeviceSummaryDTO tachographDevice;
    private CascoInsuranceSummaryDTO cascoInsurance;
    private FireExtinguisherSummaryDTO fireExtinguisher;
    private FuelTypeSummaryDTO fuelType;
    private VehicleModelSummaryDTO vehicleModel;
    private VehicleTypeSummaryDTO vehicleType;
    private VehicleSubtypeSummaryDTO vehicleSubtype;
    private NisCardSummaryDTO nisCard;
    private TireMarkingSummaryDTO tireMarking;
}