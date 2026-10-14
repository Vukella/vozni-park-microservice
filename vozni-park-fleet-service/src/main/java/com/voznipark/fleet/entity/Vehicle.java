package com.voznipark.fleet.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "vehicle")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_vehicle")
    private Long idVehicle;

    @Column(name = "sap_number")
    private Integer sapNumber;

    @Column(name = "chassis_number", length = 50)
    private String chassisNumber;

    @Column(name = "engine_number")
    private Integer engineNumber;

    @Column(name = "tag_serial_number")
    private Integer tagSerialNumber;

    @Column(name = "year_of_manufacture")
    private Integer yearOfManufacture;

    @Column(name = "engine_displacement", precision = 10, scale = 2)
    private BigDecimal engineDisplacement;

    @Column(name = "power")
    private Integer power;

    @Column(name = "vehicle_status", length = 20)
    private String vehicleStatus;

    @Column(name = "status_code")
    private Integer statusCode;

    // Document relations

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "registration_id")
    private Registration registration;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "first_aid_kit_id")
    private FirstAidKit firstAidKit;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tachograph_device_id")
    private TachographDevice tachographDevice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "casco_insurance_id")
    private CascoInsurance cascoInsurance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fire_extinguisher_id")
    private FireExtinguisher fireExtinguisher;

    // ─────────────────────────────────────────────────────────────
    // Reference data relations
    // ─────────────────────────────────────────────────────────────

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fuel_type_id")
    private FuelType fuelType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_model_id")
    private VehicleModel vehicleModel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_type_id")
    private VehicleType vehicleType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_subtype_id")
    private VehicleSubtype vehicleSubtype;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nis_card_id")
    private NisCard nisCard;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tire_marking_id")
    private TireMarking tireMarking;

    // ─────────────────────────────────────────────────────────────
    // Physical attributes
    // ─────────────────────────────────────────────────────────────

    @Column(name = "mass", precision = 10, scale = 2)
    private BigDecimal mass;

    @Column(name = "color", length = 20)
    private String color;

    @Column(name = "load_capacity")
    private Integer loadCapacity;

    @Column(name = "total_mass")
    private Integer totalMass;

    @Column(name = "number_of_seats")
    private Integer numberOfSeats;

    @Column(name = "first_registration_date")
    private LocalDate firstRegistrationDate;

    @JdbcTypeCode(SqlTypes.TINYINT)
    @Column(name = "operational_leasing")
    private Boolean operationalLeasing;

    @JdbcTypeCode(SqlTypes.TINYINT)
    @Column(name = "decommissioned")
    private Boolean decommissioned;

    @Column(name = "decommission_date")
    private LocalDate decommissionDate;

    @Column(name = "note", length = 300)
    private String note;
}
