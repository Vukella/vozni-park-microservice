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
    private Long       travelOrderId;  // ⚠️ INFERRED
    private Long       vehicleId;      // ⚠️ INFERRED
    private List<Long> driverIds;      // ⚠️ INFERRED
    private Long       locationId;     // ⚠️ INFERRED
    private Instant    occurredAt;     // ⚠️ INFERRED
}
