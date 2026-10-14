package com.voznipark.fleet.kafka.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

// informational event after step 1 of the saga; carries vehicleSapNumber for snapshot
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleReservedEvent {

    private Long travelOrderId;
    private Long vehicleId;
    private Integer vehicleSapNumber;
    private List<Long> driverIds;
    private Instant occurredAt;
}
