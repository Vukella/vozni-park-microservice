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
public class DriversReservedEvent {
    private Long       travelOrderId;
    private Long       vehicleId;
    private List<Long> driverIds;
    private Long       locationId;
    private Instant    occurredAt;
}
