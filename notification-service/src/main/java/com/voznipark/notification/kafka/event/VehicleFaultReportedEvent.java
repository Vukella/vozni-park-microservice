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
public class VehicleFaultReportedEvent {
    private Long    faultId;
    private Long    vehicleId;
    private Integer vehicleSapNumber;
    private String  description;
    private Boolean vehicleInoperable;
    private Long    locationId;
    private Instant occurredAt;
}
