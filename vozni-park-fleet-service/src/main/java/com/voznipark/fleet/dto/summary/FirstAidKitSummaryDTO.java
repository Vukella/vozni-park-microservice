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
public class FirstAidKitSummaryDTO {
    private Long idFirstAidKit;
    private LocalDate expiryDate;
    private String status;
    private Integer statusCode;
}