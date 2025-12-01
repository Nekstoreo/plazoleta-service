package com.pragma.plazoleta.domain.exception;

public class InvalidPriceException extends RuntimeException {

    private static final String DEFAULT_MESSAGE = "Price must be a positive integer greater than zero";

    public InvalidPriceException() {
        super(DEFAULT_MESSAGE);
    }

    public InvalidPriceException(String message) {
        super(message);
    }
}
