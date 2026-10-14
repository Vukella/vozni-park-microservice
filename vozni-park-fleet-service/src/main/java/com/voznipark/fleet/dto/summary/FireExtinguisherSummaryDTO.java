package com.voznipark.fleet.dto.summary;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FireExtinguisherSummaryDTO {
    private Long idFireExtinguisher;
    private String serialNumber;
    private LocalDate dateFrom;
    private LocalDate dateTo;
    private String status;
    private Integer statusCode;
}