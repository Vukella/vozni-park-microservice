package com.voznipark.travelorder.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * status:     PENDING | CONFIRMED | FAILED | IN_PROGRESS | COMPLETED | CANCELLED
 * sagaStatus: INITIATED | VEHICLE_RESERVED | RESOURCES_RESERVED | FAILED
 * vehicleId, createdByUserId, locationId are cross-service references — plain Long, no @ManyToOne
 */
@Entity
@Table(name = "travel_order")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TravelOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_travel_order")
    private Long idTravelOrder;

    /** Cross-service reference — Vehicle lives in fleet-service. */
    @Column(name = "vehicle_id", nullable = false)
    private Long vehicleId;

    // snapshotted from VehicleReservedEvent — carried in TravelOrderCompletedEvent
    @Column(name = "vehicle_sap_number")
    private Integer vehicleSapNumber;

    @Column(name = "date_from", nullable = false)
    private LocalDate dateFrom;

    @Column(name = "date_to", nullable = false)
    private LocalDate dateTo;

    @Column(name = "work_order_number", length = 30, unique = true)
    private String workOrderNumber;

    @Column(name = "travel_order_number", length = 20)
    private String travelOrderNumber;

    @Column(name = "location_id")
    private Integer locationId;

    @Column(name = "starting_mileage")
    private Integer startingMileage;

    @Column(name = "ending_mileage")
    private Integer endingMileage;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    /** Saga choreography step tracker. Updated by TravelOrderSagaConsumer. */
    @Column(name = "saga_status", nullable = false, length = 30)
    private String sagaStatus;

    /** Populated when status = FAILED. Describes which Saga step failed and why. */
    @Column(name = "failure_reason", length = 500)
    private String failureReason;

    @Column(name = "created_by_user_id")
    private Long createdByUserId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @OneToMany(mappedBy = "travelOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<TravelOrderDriver> drivers = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        this.createdAt = Instant.now();
        if (this.status == null) {
            this.status = "PENDING";
        }
        if (this.sagaStatus == null) {
            this.sagaStatus = "INITIATED";
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = Instant.now();
    }
}