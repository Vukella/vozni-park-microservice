package com.voznipark.travelorder.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class TravelOrderResponseDTO {

    private Long idTravelOrder;
    private Long vehicleId;
    private LocalDate dateFrom;
    private LocalDate dateTo;
    private String workOrderNumber;
    private String travelOrderNumber;
    private Integer locationId;
    private Integer startingMileage;
    private Integer endingMileage;

    /** Business status: PENDING | CONFIRMED | FAILED | IN_PROGRESS | COMPLETED | CANCELLED */
    private String status;

    /** Saga step tracker: INITIATED | VEHICLE_RESERVED | RESOURCES_RESERVED | FAILED */
    private String sagaStatus;

    /** Populated when status = FAILED. */
    private String failureReason;

    private Long createdByUserId;
    private Instant createdAt;
    private Instant updatedAt;

    private List<DriverAssignmentDTO> drivers;

    @Data
    @Builder
    public static class DriverAssignmentDTO {
        private Long driverId;
        private Boolean isPrimary;
    }
}
