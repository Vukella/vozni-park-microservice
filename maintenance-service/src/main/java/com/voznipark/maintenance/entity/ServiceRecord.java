package com.voznipark.maintenance.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;


@Entity
@Table(name = "service_record")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "vehicle_id", nullable = false)
    private Long vehicleId;

    @Column(name = "vehicle_sap_number")
    private Integer vehicleSapNumber;

    /** MINOR | MAJOR | FAULT_REPAIR */
    @Column(name = "service_type", nullable = false, length = 20)
    private String serviceType;

    /** SCHEDULED | FAULT_REPAIR | MILEAGE */
    @Column(name = "trigger_type", nullable = false, length = 20)
    private String triggerType;

    /** PENDING | IN_PROGRESS | COMPLETED | CANCELLED */
    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "source_event_reference")
    private Long sourceEventReference;

    @Column(name = "scheduled_at", nullable = false)
    private Instant scheduledAt;

    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @PrePersist
    public void prePersist() {
        if (this.status == null) {
            this.status = "PENDING";
        }
        this.createdAt = Instant.now();
        if (this.scheduledAt == null) {
            this.scheduledAt = Instant.now();
        }
    }
}
