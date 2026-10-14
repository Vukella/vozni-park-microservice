package com.voznipark.fleet.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "tachograph_device")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TachographDevice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tachograph_device")
    private Long idTachographDevice;

    @Column(name = "device_type", length = 30)
    private String deviceType;

    @Column(name = "serial_number", length = 50)
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
