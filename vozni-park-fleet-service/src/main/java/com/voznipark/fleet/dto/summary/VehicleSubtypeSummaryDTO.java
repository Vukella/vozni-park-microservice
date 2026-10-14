package com.voznipark.fleet.dto.summary;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleSubtypeSummaryDTO {
    private Long idVehicleSubtype;
    private String name;
    private VehicleTypeSummaryDTO vehicleType;
}