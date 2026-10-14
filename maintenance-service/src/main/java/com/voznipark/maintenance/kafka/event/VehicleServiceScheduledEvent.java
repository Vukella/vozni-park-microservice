package com.voznipark.maintenance.kafka.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;

// serviceType: MINOR | MAJOR — sourceEventReference will be null for SCHEDULED records
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleServiceScheduledEvent {
    private Long      vehicleId;
    private Integer   vehicleSapNumber;
    private String    serviceType;      // MINOR | MAJOR
    private LocalDate scheduledDate;
    private Long      locationId;
    private Instant   occurredAt;
}
