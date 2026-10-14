package com.voznipark.fleet.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "casco_insurance")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CascoInsurance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_casco_insurance")
    private Long idCascoInsurance;

    @Column(name = "contract_number", length = 30)
    private String contractNumber;

    @Column(name = "policy_number", length = 30)
    private String policyNumber;

    @Column(name = "date_from")
    private LocalDate dateFrom;

    @Column(name = "date_to")
    private LocalDate dateTo;

    @Column(name = "status", length = 20)
    private String status;

    @Column(name = "status_code")
    private Integer statusCode;
}
