package com.voznipark.fleet.kafka.event;

import lombok.*;

import java.time.Instant;
import java.util.List;

// Fleet-side copy — same fields as travel-order-service TravelOrderCompletedEvent
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TravelOrderCompletedEvent {

    private Long travelOrderId;
    private String travelOrderNumber;
    private Long vehicleId;
    private Integer vehicleSapNumber;
    private List<Long> driverIds;
    private Integer startingMileage;
    private Integer endingMileage;
    private Integer distanceKm;
    private Long locationId;
    private Instant occurredAt;
}