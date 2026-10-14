package com.voznipark.fleet.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "brand")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Brand {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_brand")
    private Long idBrand;

    @Column(name = "name", nullable = false, length = 30)
    private String name;
}
