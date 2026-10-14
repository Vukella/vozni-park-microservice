package com.voznipark.fleet.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "vehicle_assigned_driver_log")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VehicleAssignedDriverLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_vehicle_assigned_driver_log")
    private Long idVehicleAssignedDriverLog;

    @Column(name = "previous_driver_id")
    private Long previousDriverId;

    @Column(name = "new_driver_id", nullable = false)
    private Long newDriverId;

    @Column(name = "change_date", nullable = false)
    private LocalDate changeDate;

    @Column(name = "changed_by_user_id", nullable = false)
    private Long changedByUserId;

    // vehicle_id has FK to vehicle
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;
}