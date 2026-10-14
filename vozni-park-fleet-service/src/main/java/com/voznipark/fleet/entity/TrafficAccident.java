package com.voznipark.fleet.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "traffic_accident")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrafficAccident {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_traffic_accident")
    private Long idTrafficAccident;

    @Column(name = "accident_date")
    private LocalDate accidentDate;

    @Column(name = "accident_time", length = 10)
    private String accidentTime;

    @Column(name = "location_description", length = 200)
    private String locationDescription;

    @Column(name = "accident_description", length = 2000)
    private String accidentDescription;

    @Column(name = "damage_description", length = 1000)
    private String damageDescription;

    /** false = not reported, true = reported to police. */
    @JdbcTypeCode(SqlTypes.TINYINT)
    @Column(name = "reported_to_police")
    private Boolean reportedToPolice = false;

    @Column(name = "not_reported_reason", length = 500)
    private String notReportedReason;

    @Column(name = "estimated_damage_value", precision = 15, scale = 2)
    private BigDecimal estimatedDamageValue;

    // Snapshot fields — immutable after creation

    @Column(name = "driver_license_number_snapshot", length = 30)
    private String driverLicenseNumberSnapshot;

    @Column(name = "driver_license_expiry_snapshot")
    private LocalDate driverLicenseExpirySnapshot;

    @Column(name = "statement_given_by_driver_id")
    private Long statementGivenByDriverId;

    @Column(name = "travel_order_number_snapshot", length = 30)
    private String travelOrderNumberSnapshot;

    @Column(name = "other_participants", length = 1000)
    private String otherParticipants;

    @Column(name = "declarant_national_id", length = 13)
    private String declarantNationalId;

    @Column(name = "declarant_address", length = 200)
    private String declarantAddress;

    @Column(name = "declarant_email", length = 100)
    private String declarantEmail;

    @Column(name = "declarant_position", length = 100)
    private String declarantPosition;

    /** Base64-encoded PNG signature image. */
    @Column(name = "signature", columnDefinition = "TEXT")
    private String signature;

    @Column(name = "created_by_user_id")
    private Long createdByUserId;

    // FK relationships

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    // driver_id nullable — accident may be recorded before driver is identified
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id")
    private Driver driver;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id")
    private LocationUnit location;
}