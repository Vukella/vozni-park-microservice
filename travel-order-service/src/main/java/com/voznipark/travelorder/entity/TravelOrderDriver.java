package com.voznipark.travelorder.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/**
 * Associates a Driver (cross-service reference) with a TravelOrder.
 * isPrimary = true marks the driver responsible for the order.
 *
 * driverId is a plain Long — Driver lives in fleet-service,
 * so there is no @ManyToOne JPA relationship here.
 *
 * The dual-column pattern (travelOrderId mirror field) matches the
 * established fleet-service convention for FK access without lazy loading.
 */
@Entity
@Table(name = "travel_order_driver",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_tod_travel_order_driver",
                columnNames = {"travel_order_id", "driver_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TravelOrderDriver {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_travel_order_driver")
    private Long idTravelOrderDriver;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "travel_order_id", nullable = false)
    private TravelOrder travelOrder;

    /** Mirror field for the FK — read-only, avoids loading the full TravelOrder just to get its ID. */
    @Column(name = "travel_order_id", insertable = false, updatable = false, nullable = false)
    private Long travelOrderId;

    @Column(name = "driver_id", nullable = false)
    private Long driverId;

    @Column(name = "is_primary", nullable = false)
    @JdbcTypeCode(SqlTypes.TINYINT)
    private Boolean isPrimary;
}
