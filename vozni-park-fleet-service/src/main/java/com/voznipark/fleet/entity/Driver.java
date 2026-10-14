package com.voznipark.fleet.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "driver")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Driver {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_driver")
    private Long idDriver;

    @Column(name = "sap_number", nullable = false, unique = true)
    private Integer sapNumber;

    @Column(name = "full_name", length = 60)
    private String fullName;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "status", length = 30)
    private String status;

    @Column(name = "status_code")
    private Integer statusCode;

    // soft reference to monolith employee — not a real FK
    @Column(name = "zaposleni_id")
    private Long zaposleniId;

    // 1 = revoked; full history is in license_revocation table
    @JdbcTypeCode(SqlTypes.TINYINT)
    @Column(name = "license_revoked", nullable = false)
    private Boolean licenseRevoked;

    // Document relations

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medical_certificate_id")
    private MedicalCertificate medicalCertificate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_permit_id")
    private DriverPermit driverPermit;
}
