package com.voznipark.fleet.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "location_unit")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LocationUnit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_location_unit")
    private Long idLocationUnit;

    @Column(name = "location_address", length = 500)
    private String locationAddress;

    @Column(name = "location_name", nullable = false, length = 100)
    private String locationName;

    /** Self-referential FK for location hierarchy. NULL = top-level location. **/
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private LocationUnit parent;
}
