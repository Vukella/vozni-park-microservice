package com.voznipark.fleet.exception;

public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException(String entityType, Long id) {
        super(entityType + " with id " + id + " not found");
    }
    public EntityNotFoundException(String message) {
        super(message);
    }
}
