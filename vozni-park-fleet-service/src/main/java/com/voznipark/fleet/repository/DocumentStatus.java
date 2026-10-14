package com.voznipark.fleet.enums;

import java.util.Arrays;


public enum DocumentStatus {

    EXPIRED(0),
    VALID(1);

    private final int code;

    DocumentStatus(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static DocumentStatus fromCode(int code) {
        return Arrays.stream(values())
                .filter(status -> status.code == code)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown DocumentStatus code: " + code));
    }
}
