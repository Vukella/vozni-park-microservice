package com.voznipark.fleet.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "fuel_type")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FuelType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_fuel_type")
    private Long idFuelType;

    @Column(name = "name", length = 30)
    private String name;
}
