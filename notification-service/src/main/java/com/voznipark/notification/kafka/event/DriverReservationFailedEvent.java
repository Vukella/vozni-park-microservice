package com.voznipark.notification.kafka.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DriverReservationFailedEvent {
    private Long       travelOrderId;
    private Long       vehicleId;             // vehicle released as compensating transaction
    private List<Long> requestedDriverIds;    // confirmed: requestedDriverIds, NOT driverIds
    private String     reason;
    private Long       locationId;
    private Instant    occurredAt;
}