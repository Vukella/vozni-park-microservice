package com.voznipark.fleet.kafka.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DriverStatusChangedEvent {
    private Long    driverId;
    private Integer sapNumber;
    private String  fullName;
    private String  oldStatus;
    private String  newStatus;
    private Long    locationId;
    private Instant occurredAt;
}
