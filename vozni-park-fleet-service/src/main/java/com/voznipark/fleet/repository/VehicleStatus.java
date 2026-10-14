package com.voznipark.fleet.enums;

import java.util.Arrays;

/**
 * Priority order (highest first): IN_FAULT ; INVALID ; UNAVAILABLE ; ON_DUTY ; AVAILABLE.
 * See StatusCalculationService.calculateVehicleStatus() for the calculation logic.
 */
public enum VehicleStatus {

    AVAILABLE(1),
    ON_DUTY(2),
    UNAVAILABLE(3),
    INVALID(4),
    IN_FAULT(5);

    private final int code;

    VehicleStatus(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static VehicleStatus fromCode(int code) {
        return Arrays.stream(values())
                .filter(status -> status.code == code)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown VehicleStatus code: " + code));
    }
}
