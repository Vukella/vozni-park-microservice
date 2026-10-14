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
public class DriverLicenseRestoredEvent {
    private Long    driverId;
    private Integer driverSapNumber;
    private String  fullName;
    private Long    revocationId;
    private Long    locationId;
    private Instant restoredAt;
}
