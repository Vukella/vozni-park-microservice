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
public class RegistrationNumberChangedEvent {
    private Long    vehicleId;
    private Integer vehicleSapNumber;
    private String  oldRegistrationNumber;
    private String  newRegistrationNumber;
    private Long    locationId;
    private Instant changedAt;
}
