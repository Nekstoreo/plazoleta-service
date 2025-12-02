package com.pragma.plazoleta.domain.exception;

public class DishNotFromRestaurantException extends RuntimeException {

    public DishNotFromRestaurantException(Long dishId, Long restaurantId) {
        super("El plato con ID " + dishId + " no pertenece al restaurante con ID " + restaurantId + ".");
    }
}
