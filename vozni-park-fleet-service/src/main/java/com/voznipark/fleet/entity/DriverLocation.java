package com.voznipark.fleet.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "driver_location")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DriverLocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_driver_location")
    private Long idDriverLocation;

    // Owning side — Driver has @OneToOne(mappedBy = "driver")
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id", nullable = false)
    private Driver driver;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_unit_id", nullable = false)
    private LocationUnit locationUnit;
}