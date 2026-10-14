package com.voznipark.reporting.exception;

public class EntityNotFoundException extends RuntimeException {

    public EntityNotFoundException(String entityName, Long id) {
        super(entityName + " with id " + id + " not found");
    }

    public EntityNotFoundException(String entityName, String reference) {
        super(entityName + " with reference '" + reference + "' not found");
    }
}
