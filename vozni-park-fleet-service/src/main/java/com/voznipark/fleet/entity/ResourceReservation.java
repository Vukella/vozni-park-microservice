package com.voznipark.fleet.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "resource_reservation")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResourceReservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_resource_reservation")
    private Long idResourceReservation;

    @Column(name = "travel_order_id", nullable = false)
    private Long travelOrderId;

    // "VEHICLE" or "DRIVER"
    @Column(name = "resource_type", nullable = false, length = 10)
    private String resourceType;

    @Column(name = "resource_id", nullable = false)
    private Long resourceId;

    @Column(name = "reserved_at", nullable = false)
    private Instant reservedAt;

    // NULL means the reservation is active
    @Column(name = "released_at")
    private Instant releasedAt;

    // "COMPENSATION" or "COMPLETED"
    @Column(name = "release_reason", length = 20)
    private String releaseReason;
}