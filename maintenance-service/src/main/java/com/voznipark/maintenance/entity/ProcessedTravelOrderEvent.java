package com.voznipark.maintenance.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

// deduplication guard — UNIQUE on travel_order_id prevents duplicate event processing
@Entity
@Table(
        name = "processed_travel_order_event",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_processed_travel_order_event_travel_order_id",
                columnNames = "travel_order_id"
        )
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProcessedTravelOrderEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "travel_order_id", nullable = false)
    private Long travelOrderId;

    @Column(name = "processed_at", nullable = false)
    private Instant processedAt;
}