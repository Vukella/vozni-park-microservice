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
public class VehicleDocumentUploadedEvent {
    private Long    documentId;
    private Long    vehicleId;
    private Integer vehicleSapNumber;
    private String  fileName;
    private String  contentType;
    private String  documentType;
    private Instant uploadedAt;
}
