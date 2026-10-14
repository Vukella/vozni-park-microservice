package com.voznipark.travelorder.exception;

public class EntityNotFoundException extends RuntimeException {

    public EntityNotFoundException(String entityName, Long id) {
        super(entityName + " with id " + id + " not found");
    }
}
