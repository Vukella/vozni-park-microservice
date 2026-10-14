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
public class TrafficAccidentReportedEvent {
    private Long    accidentId;
    private Long    vehicleId;
    private Integer vehicleSapNumber;
    private Long    driverId;
    private String  travelOrderNumberSnapshot;
    private String  driverLicenseNumberSnapshot;
    private Long    locationId;
    private Instant reportedAt;
}
