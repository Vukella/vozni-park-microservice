package com.voznipark.fleet.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tire_marking")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TireMarking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tire_marking")
    private Long idTireMarking;

    @Column(name = "name", length = 100)
    private String name;

    @Column(name = "marking", length = 100)
    private String marking;
}
