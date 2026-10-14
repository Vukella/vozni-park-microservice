package com.voznipark.fueltracking.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FuelSummaryDTO {
    private Long       totalRecords;
    private Long       totalDistanceKm;          // SUM of Integer → Long in JPQL
    private BigDecimal totalFuelConsumedLiters;
    private BigDecimal totalFuelCost;
}