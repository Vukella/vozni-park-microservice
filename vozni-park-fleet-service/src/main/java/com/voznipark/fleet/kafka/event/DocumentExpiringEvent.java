package com.voznipark.fleet.kafka.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;

// Daily refresh publishes this when a document nears expiry.
// Covers all 9 document types for both vehicles and drivers.
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentExpiringEvent {
    private String    entityType;
    private Long      entityId;
    private String    documentType;
    private LocalDate expiryDate;
    private Integer   daysUntilExpiry;
    private Long      locationId;
    private Instant   occurredAt;
}
