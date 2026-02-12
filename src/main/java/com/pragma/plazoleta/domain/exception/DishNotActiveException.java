package com.pragma.plazoleta.domain.exception;

public class DishNotActiveException extends RuntimeException {

    public DishNotActiveException(Long dishId) {
        super("Dish with ID " + dishId + " is not currently available.");
    }
}
