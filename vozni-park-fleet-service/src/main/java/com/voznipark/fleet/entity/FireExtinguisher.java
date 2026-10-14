package com.voznipark.fleet.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "fire_extinguisher")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FireExtinguisher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_fire_extinguisher")
    private Long idFireExtinguisher;

    @Column(name = "serial_number", nullable = false, length = 50)
    private String serialNumber;

    @Column(name = "date_from")
    private LocalDate dateFrom;

    @Column(name = "date_to")
    private LocalDate dateTo;

    @Column(name = "status", length = 20)
    private String status;

    @Column(name = "status_code")
    private Integer statusCode;
}
