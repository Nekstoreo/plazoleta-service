package com.pragma.plazoleta.domain.exception;

public class RestaurantAlreadyExistsException extends RuntimeException {

    public RestaurantAlreadyExistsException(String message) {
        super(message);
    }
}
