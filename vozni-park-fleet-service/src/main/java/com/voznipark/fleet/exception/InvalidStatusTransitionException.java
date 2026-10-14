package com.voznipark.fleet.exception;

public class InvalidStatusTransitionException extends RuntimeException {
    public InvalidStatusTransitionException(String from, String to) {
        super("Cannot transition status from " + from + " to " + to);
    }
}
