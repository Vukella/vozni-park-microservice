package com.voznipark.fleet.dto.summary;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BrandSummaryDTO {
    private Long idBrand;
    private String name;
}