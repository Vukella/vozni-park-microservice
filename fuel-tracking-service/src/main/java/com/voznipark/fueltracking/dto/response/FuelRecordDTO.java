package com.voznipark.fueltracking.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FuelRecordDTO {
    private Long       id;
    private Long       travelOrderId;
    private String     travelOrderNumber;
    private Long       vehicleId;
    private Integer    vehicleSapNumber;
    private Integer    distanceKm;
    private BigDecimal fuelConsumedLiters;
    private BigDecimal pricePerLiter;
    private BigDecimal totalFuelCost;
    private Instant    recordedAt;
}