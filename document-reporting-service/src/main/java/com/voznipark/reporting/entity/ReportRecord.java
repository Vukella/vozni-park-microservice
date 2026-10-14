package com.voznipark.reporting.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

/**
 * report_type: TRAVEL_ORDER_COMPLETION | TRAFFIC_ACCIDENT
 * PDF binary stored as LONGBLOB alongside metadata.
 */
@Entity
@Table(name = "report_record")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "report_type", length = 30, nullable = false)
    private String reportType;

    @Column(name = "source_entity_id")
    private Long sourceEntityId;

    @Column(name = "source_reference", length = 100)
    private String sourceReference;

    @Column(name = "vehicle_sap_number")
    private Integer vehicleSapNumber;

    @Column(name = "generated_at")
    private LocalDateTime generatedAt;

    @Column(name = "file_name", length = 255)
    private String fileName;

    @Column(name = "content_type", length = 100)
    private String contentType;

    @Column(name = "file_size")
    private Long fileSize;

    @JdbcTypeCode(SqlTypes.LONGVARBINARY)
    @Column(name = "report_content", nullable = false)
    private byte[] reportContent;

    @PrePersist
    public void prePersist() {
        if (generatedAt == null) {
            generatedAt = LocalDateTime.now();
        }
        if (contentType == null) {
            contentType = "application/pdf";
        }
    }
}
