package com.voznipark.maintenance.kafka.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

// only vehicleInoperable=true events trigger a ServiceRecord in this service
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleFaultReportedEvent {
    private Long    faultId;
    private Long    vehicleId;
    private Integer vehicleSapNumber;
    private String  description;
    private Boolean vehicleInoperable;
    private Long    locationId;
    private Instant occurredAt;
}
