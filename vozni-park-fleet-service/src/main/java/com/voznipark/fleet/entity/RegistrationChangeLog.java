package com.voznipark.fleet.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "registration_change_log")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationChangeLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_registration_change_log")
    private Long idRegistrationChangeLog;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    @Column(name = "old_registration_number", length = 20)
    private String oldRegistrationNumber;

    @Column(name = "new_registration_number", nullable = false, length = 20)
    private String newRegistrationNumber;

    @Column(name = "change_reason", nullable = false)
    private Integer changeReason;

    @Column(name = "change_note", length = 300)
    private String changeNote;

    @Column(name = "change_date", nullable = false)
    private LocalDate changeDate;

    @Column(name = "changed_by_user_id")
    private Long changedByUserId;
}