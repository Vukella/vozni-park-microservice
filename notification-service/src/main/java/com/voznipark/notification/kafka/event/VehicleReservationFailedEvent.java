package com.voznipark.notification.kafka.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleReservationFailedEvent {
    private Long    travelOrderId;
    private Long    vehicleId;
    private String  reason;
    private Long    locationId;
    private Instant occurredAt;
}
