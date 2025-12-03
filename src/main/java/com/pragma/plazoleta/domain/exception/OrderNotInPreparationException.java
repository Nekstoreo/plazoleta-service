package com.pragma.plazoleta.domain.exception;

public class OrderNotInPreparationException extends RuntimeException {

    public OrderNotInPreparationException(Long orderId) {
        super(String.format("Order with id %d is not in preparation state and cannot be marked as ready", orderId));
    }
}
