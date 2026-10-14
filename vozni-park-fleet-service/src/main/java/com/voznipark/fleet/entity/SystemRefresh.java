package com.voznipark.fleet.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "system_refresh")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SystemRefresh {

    @Id
    @Column(name = "key_name", length = 50)
    private String keyName;

    @Column(name = "last_run_ts")
    private LocalDateTime lastRunTs;

    @Column(name = "last_run_status", length = 10)
    private String lastRunStatus;

    @Column(name = "last_run_message", length = 500)
    private String lastRunMessage;

    @Column(name = "last_run_by", length = 100)
    private String lastRunBy;
}
