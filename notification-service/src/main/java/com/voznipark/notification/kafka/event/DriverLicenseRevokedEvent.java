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
public class DriverLicenseRevokedEvent {
    private Long    driverId;
    private Long    locationId;
    private Instant revokedAt;
}
