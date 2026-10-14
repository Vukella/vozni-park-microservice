package com.voznipark.fleet.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/** PUT semantics — null relation ID clears the relation. Status fields intentionally absent. */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleRequestDTO {

    @NotNull
    private Integer sapNumber;

    @NotBlank
    @Size(max = 50)
    private String chassisNumber;

    private Integer engineNumber;

    private Integer tagSerialNumber;

    private Integer yearOfManufacture;

    private BigDecimal engineDisplacement;

    private Integer power;

    // Document relations
    private Long registrationId;
    private Long firstAidKitId;
    private Long tachographDeviceId;
    private Long cascoInsuranceId;
    private Long fireExtinguisherId;

    // Reference data relations
    private Long fuelTypeId;
    private Long vehicleModelId;
    private Long vehicleTypeId;
    private Long vehicleSubtypeId;
    private Long nisCardId;
    private Long tireMarkingId;

    // Physical attributes
    private BigDecimal mass;

    @Size(max = 20)
    private String color;

    private Integer loadCapacity;

    private Integer totalMass;

    private Integer numberOfSeats;

    private LocalDate firstRegistrationDate;

    private Boolean operationalLeasing;

    @Size(max = 300)
    private String note;
}