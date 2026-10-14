package com.voznipark.travelorder.kafka.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

// compensation acknowledgement — logged only, no state change
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleReleasedEvent {

    private Long travelOrderId;
    private Long vehicleId;
    private String reason;
    private Instant occurredAt;
}
