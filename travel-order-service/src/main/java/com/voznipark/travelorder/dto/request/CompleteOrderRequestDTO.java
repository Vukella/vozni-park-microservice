package com.voznipark.travelorder.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

// service enforces endingMileage > startingMileage
public record CompleteOrderRequestDTO(
        @NotNull(message = "endingMileage is required")
        @Min(value = 0, message = "endingMileage must be >= 0")
        Integer endingMileage
) {}
