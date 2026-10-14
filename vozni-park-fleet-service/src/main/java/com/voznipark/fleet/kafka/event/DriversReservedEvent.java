package com.voznipark.fleet.kafka.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

// saga success — all drivers reserved, travel-order-service confirms the order
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DriversReservedEvent {

    private Long travelOrderId;
    private Long vehicleId;
    private List<Long> driverIds;
    private Instant occurredAt;
}
