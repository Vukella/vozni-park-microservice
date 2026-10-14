package com.voznipark.travelorder.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class TravelOrderRequestDTO {

    @NotNull(message = "vehicleId is required")
    private Long vehicleId;

    @NotEmpty(message = "At least one driver is required")
    private List<Long> driverIds;

    /** One of the driverIds designated as the responsible driver. Optional — first driver used if null. */
    private Long primaryDriverId;

    @NotNull(message = "dateFrom is required")
    private LocalDate dateFrom;

    @NotNull(message = "dateTo is required")
    private LocalDate dateTo;

    private String workOrderNumber;

    private Integer locationId;

    private Long createdByUserId;
}
