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
public class RegistrationSummaryDTO {
    private Long idRegistration;
    private String registrationNumber;
    private LocalDate dateFrom;
    private LocalDate dateTo;
    private Integer policyNumber;
    private String status;
    private Integer statusCode;
}