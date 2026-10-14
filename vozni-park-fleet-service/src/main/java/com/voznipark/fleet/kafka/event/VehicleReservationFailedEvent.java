package com.voznipark.fleet.kafka.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

// no compensation needed — vehicle was never reserved
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleReservationFailedEvent {

    private Long travelOrderId;
    private Long vehicleId;
    private String reason;
    private Instant occurredAt;
}
