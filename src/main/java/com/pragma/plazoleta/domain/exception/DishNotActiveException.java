package com.pragma.plazoleta.domain.exception;

public class DishNotActiveException extends RuntimeException {

    public DishNotActiveException(Long dishId) {
        super("El plato con ID " + dishId + " no está disponible actualmente.");
    }
}
