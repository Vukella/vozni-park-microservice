package com.voznipark.fleet.kafka.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

// snapshot fields capture values at recording time, not current document state
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrafficAccidentReportedEvent {
    private Long    accidentId;
    private Long    vehicleId;
    private Integer vehicleSapNumber;
    private Long    driverId;
    private String  travelOrderNumberSnapshot;    // value at recording time
    private String  driverLicenseNumberSnapshot;  // value at recording time
    private Long    locationId;
    private Instant reportedAt;
}
