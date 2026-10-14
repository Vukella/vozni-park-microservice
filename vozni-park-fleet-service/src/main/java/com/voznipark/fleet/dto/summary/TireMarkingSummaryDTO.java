package com.voznipark.fleet.dto.summary;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TireMarkingSummaryDTO {
    private Long idTireMarking;
    private String name;
    private String marking;
}