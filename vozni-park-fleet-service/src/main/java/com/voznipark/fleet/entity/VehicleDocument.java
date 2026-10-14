package com.voznipark.fleet.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "vehicle_document")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VehicleDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_vehicle_document")
    private Long idVehicleDocument;

    /** TRAFFIC_LICENSE, CERTIFICATE, EXPERT_FINDING */
    @Column(name = "document_type", nullable = false, length = 30)
    private String documentType;

    @Column(name = "file_name", nullable = false, length = 255)
    private String fileName;

    @Column(name = "content_type", length = 100)
    private String contentType;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "storage_path", nullable = false, length = 500)
    private String storagePath;

    @Column(name = "upload_date")
    private LocalDate uploadDate;

    @Column(name = "uploaded_by_user_id")
    private Long uploadedByUserId;

    @Column(name = "notes", length = 300)
    private String notes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;
}