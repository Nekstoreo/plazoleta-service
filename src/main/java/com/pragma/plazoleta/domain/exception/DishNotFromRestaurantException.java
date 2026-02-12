package com.pragma.plazoleta.domain.exception;

public class DishNotFromRestaurantException extends RuntimeException {

    public DishNotFromRestaurantException(Long dishId, Long restaurantId) {
        super("Dish with ID " + dishId + " does not belong to restaurant with ID " + restaurantId + ".");
    }
}
