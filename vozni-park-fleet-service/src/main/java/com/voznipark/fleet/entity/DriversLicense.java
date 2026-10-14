package com.voznipark.fleet.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;

@Entity
@Table(name = "drivers_license")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DriversLicense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_drivers_license")
    private Long idDriversLicense;

    @Column(name = "date_from")
    private LocalDate dateFrom;

    @Column(name = "date_to")
    private LocalDate dateTo;

    @Column(name = "status", length = 20)
    private String status;

    @Column(name = "status_code")
    private Integer statusCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tachograph_card_id")
    private TachographCard tachographCard;

    @JdbcTypeCode(SqlTypes.TINYINT)
    @Column(name = "medical_restrictions")
    private Boolean medicalRestrictions;
}
