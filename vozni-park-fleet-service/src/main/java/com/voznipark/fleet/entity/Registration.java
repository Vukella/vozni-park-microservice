package com.voznipark.fleet.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "registration")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Registration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_registration")
    private Long idRegistration;

    @Column(name = "registration_number", length = 20)
    private String registrationNumber;

    @Column(name = "date_from")
    private LocalDate dateFrom;

    @Column(name = "date_to")
    private LocalDate dateTo;

    @Column(name = "policy_number")
    private Integer policyNumber;

    @Column(name = "status", length = 20)
    private String status;

    @Column(name = "status_code")
    private Integer statusCode;
}
