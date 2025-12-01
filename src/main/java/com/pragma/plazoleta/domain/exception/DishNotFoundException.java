package com.pragma.plazoleta.domain.exception;

public class DishNotFoundException extends RuntimeException {

    public DishNotFoundException(Long dishId) {
        super("Dish not found with id: " + dishId);
    }

    public DishNotFoundException(String message) {
        super(message);
    }
}
