package com.voznipark.fleet.kafka.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleFaultResolvedEvent {
    private Long    faultId;
    private Long    vehicleId;
    private Integer vehicleSapNumber;
    private Long    locationId;
    private Instant resolvedAt;
}
