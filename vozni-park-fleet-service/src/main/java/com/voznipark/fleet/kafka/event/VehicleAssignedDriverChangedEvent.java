package com.voznipark.fleet.kafka.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

// oldDriverId null = first assignment, newDriverId null = unassignment
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleAssignedDriverChangedEvent {
    private Long    vehicleId;
    private Integer vehicleSapNumber;
    private Long    oldDriverId;   // null if first assignment
    private Long    newDriverId;   // null if unassignment
    private Long    locationId;
    private Instant assignedAt;
}
