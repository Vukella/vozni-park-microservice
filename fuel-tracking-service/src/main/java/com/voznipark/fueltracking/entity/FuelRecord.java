package com.voznipark.fueltracking.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "fuel_record", indexes = {
        @Index(name = "idx_fuel_record_travel_order", columnList = "travel_order_id"),
        @Index(name = "idx_fuel_record_vehicle",      columnList = "vehicle_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FuelRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "travel_order_id", nullable = false, unique = true)
    private Long travelOrderId;

    @Column(name = "travel_order_number", length = 100)
    private String travelOrderNumber;

    @Column(name = "vehicle_id")
    private Long vehicleId;

    @Column(name = "vehicle_sap_number")
    private Integer vehicleSapNumber;

    @Column(name = "distance_km", nullable = false)
    private Integer distanceKm;

    @Column(name = "fuel_consumed_liters", nullable = false, precision = 10, scale = 2)
    private BigDecimal fuelConsumedLiters;

    @Column(name = "price_per_liter", nullable = false, precision = 10, scale = 2)
    private BigDecimal pricePerLiter;

    @Column(name = "total_fuel_cost", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalFuelCost;

    @Column(name = "recorded_at", nullable = false)
    private Instant recordedAt;
}