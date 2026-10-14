package com.voznipark.reporting.dto.response;

import java.time.LocalDateTime;

public record ReportRecordMetadataDTO(
        Long id,
        String reportType,
        Long sourceEntityId,
        String sourceReference,
        Integer vehicleSapNumber,
        LocalDateTime generatedAt,
        String fileName,
        String contentType,
        Long fileSize
) {}
