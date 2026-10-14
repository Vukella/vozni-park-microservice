package com.voznipark.travelorder.kafka.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

// saga failure — vehicle compensation already complete
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DriverReservationFailedEvent {

    private Long travelOrderId;
    private Long vehicleId;
    private List<Long> requestedDriverIds;
    private String reason;
    private Instant occurredAt;
}
