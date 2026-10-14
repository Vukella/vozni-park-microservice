package com.voznipark.fleet.kafka.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

// fleet-service listens to this to kick off vehicle + driver reservation
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TravelOrderRequestedEvent {

    private Long travelOrderId;
    private Long vehicleId;
    private List<Long> driverIds;
    private LocalDate dateFrom;
    private LocalDate dateTo;
    private String workOrderNumber;
    private Long createdByUserId;
    private Instant occurredAt;
}
