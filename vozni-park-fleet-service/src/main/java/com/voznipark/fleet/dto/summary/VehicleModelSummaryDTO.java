package com.voznipark.fleet.dto.summary;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleModelSummaryDTO {
    private Long idVehicleModel;
    private String name;
    private BrandSummaryDTO brand;
}