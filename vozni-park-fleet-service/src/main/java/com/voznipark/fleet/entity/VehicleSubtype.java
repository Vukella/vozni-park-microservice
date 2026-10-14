package com.voznipark.fleet.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "vehicle_subtype")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleSubtype {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_vehicle_subtype")
    private Long idVehicleSubtype;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_type_id", nullable = false)
    private VehicleType vehicleType;
}
