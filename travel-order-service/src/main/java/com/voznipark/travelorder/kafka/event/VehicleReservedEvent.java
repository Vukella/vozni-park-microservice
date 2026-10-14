package com.voznipark.travelorder.kafka.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

// vehicleSapNumber snapshotted here for TravelOrderCompletedEvent
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