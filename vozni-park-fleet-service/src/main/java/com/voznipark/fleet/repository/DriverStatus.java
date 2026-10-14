package com.voznipark.fleet.enums;

import java.util.Arrays;

/**
 * Priority order (highest first): SUSPENDED &gt; INVALID_DOCUMENTS &gt; ON_DUTY &gt; AVAILABLE.
 * See StatusCalculationService.calculateDriverStatus() for the calculation logic.
 */
public enum DriverStatus {

    AVAILABLE(1),
    ON_DUTY(2),
    INVALID_DOCUMENTS(3),
    SUSPENDED(4);

    private final int code;

    DriverStatus(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    // both block saga reservation, different resolution paths
    public boolean preventsAssignment() {
        return this == SUSPENDED || this == INVALID_DOCUMENTS;
    }

    public static DriverStatus fromCode(int code) {
        return Arrays.stream(values())
                .filter(status -> status.code == code)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown DriverStatus code: " + code));
    }
}
