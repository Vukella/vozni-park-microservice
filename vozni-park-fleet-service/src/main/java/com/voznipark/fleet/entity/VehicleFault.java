package com.voznipark.fleet.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;

@Entity
@Table(name = "vehicle_fault")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleFault {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_vehicle_fault")
    private Long idVehicleFault;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    @Column(name = "report_date")
    private LocalDate reportDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id")
    private LocationUnit location;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reported_by_driver_id", nullable = false)
    private Driver reportedByDriver;

    @Column(name = "mileage_at_fault")
    private Integer mileageAtFault;

    @Column(name = "fault_description", length = 1000)
    private String faultDescription;

    @JdbcTypeCode(SqlTypes.TINYINT)
    @Column(name = "tow_required")
    private Boolean towRequired;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "handover_driver_id")
    private Driver handoverDriver;

    /** 1 = changes vehicle status to IN_FAULT.  */
    @JdbcTypeCode(SqlTypes.TINYINT)
    @Column(name = "vehicle_inoperable", nullable = false)
    private Boolean vehicleInoperable;

    /** MINOR or MAJOR for scheduled services, NULL for ordinary faults.  */
    @Column(name = "service_type", length = 10)
    private String serviceType;

    @Column(name = "resolution_date")
    private LocalDate resolutionDate;

    @Column(name = "handover_date")
    private LocalDate handoverDate;

    @Column(name = "created_by_user_id")
    private Long createdByUserId;

    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    @Column(name = "signature")
    private String signature;
}
