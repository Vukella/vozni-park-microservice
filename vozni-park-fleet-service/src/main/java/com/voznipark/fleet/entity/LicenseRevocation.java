package com.voznipark.fleet.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "license_revocation")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LicenseRevocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_license_revocation")
    private Long idLicenseRevocation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id", nullable = false)
    private Driver driver;

    @Column(name = "revocation_date", nullable = false)
    private LocalDate revocationDate;

    @Column(name = "revocation_reason", nullable = false, length = 500)
    private String revocationReason;

    @Column(name = "restoration_date")
    private LocalDate restorationDate;

    @Column(name = "revoked_by_user_id", nullable = false)
    private Long revokedByUserId;

    @Column(name = "restored_by_user_id")
    private Long restoredByUserId;
}
