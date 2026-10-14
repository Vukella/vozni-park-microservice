package com.voznipark.travelorder.kafka.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

// distanceKm = endingMileage - startingMileage, computed at publish time
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TravelOrderCompletedEvent {
    private Long        travelOrderId;
    private String      travelOrderNumber;
    private Long        vehicleId;
    private Integer     vehicleSapNumber;
    private List<Long>  driverIds;
    private Integer     startingMileage;
    private Integer     endingMileage;
    private Integer     distanceKm;
    private Long        locationId;
    private Instant     occurredAt;
}
