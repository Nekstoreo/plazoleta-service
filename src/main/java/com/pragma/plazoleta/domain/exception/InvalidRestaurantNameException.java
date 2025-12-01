package com.pragma.plazoleta.domain.exception;

public class InvalidRestaurantNameException extends RuntimeException {

    public InvalidRestaurantNameException(String message) {
        super(message);
    }
}
