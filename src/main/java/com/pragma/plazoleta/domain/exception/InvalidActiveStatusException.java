package com.pragma.plazoleta.domain.exception;

public class InvalidActiveStatusException extends RuntimeException {

    public InvalidActiveStatusException() {
        super("Active flag must be provided");
    }

    public InvalidActiveStatusException(String message) {
        super(message);
    }
}
