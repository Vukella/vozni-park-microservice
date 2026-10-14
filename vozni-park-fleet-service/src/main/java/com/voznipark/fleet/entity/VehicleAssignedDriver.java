package com.voznipark.fleet.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "vehicle_assigned_driver")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VehicleAssignedDriver {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_vehicle_assigned_driver")
    private Long idVehicleAssignedDriver;

    @Column(name = "assignment_date")
    private LocalDate assignmentDate;

    @Column(name = "note", length = 500)
    private String note;

    // vehicle_id is UNIQUE — enforces 1:1 vehicle → assigned driver
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id", nullable = false)
    private Driver driver;
}