package com.voznipark.fleet.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "nis_card")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NisCard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_nis_card")
    private Long idNisCard;

    @Column(name = "card_number", nullable = false, length = 30)
    private String cardNumber;
}
