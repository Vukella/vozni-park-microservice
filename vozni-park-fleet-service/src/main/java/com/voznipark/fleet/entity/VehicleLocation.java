package com.voznipark.fleet.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "vehicle_location")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VehicleLocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_vehicle_location")
    private Long idVehicleLocation;

    // Owning side — Vehicle has @OneToOne(mappedBy = "vehicle")
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_unit_id", nullable = false)
    private LocationUnit locationUnit;
}