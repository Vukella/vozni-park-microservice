package com.voznipark.fleet.exception;

public class LocationAccessDeniedException extends RuntimeException {
    public LocationAccessDeniedException(Long locationId) {
        super("Access denied for location " + locationId);
    }
}
