package com.voznipark.fleet.kafka.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleDecommissionedEvent {
    private Long      vehicleId;
    private Integer   vehicleSapNumber;
    private LocalDate decommissionDate;
    private Long      locationId;
    private Instant   occurredAt;
}
