package com.pragma.plazoleta.domain.exception;

public class InvalidOrderStatusException extends RuntimeException {

    public InvalidOrderStatusException(String message) {
        super(message);
    }

    public InvalidOrderStatusException(Long orderId, String currentStatus) {
        super("Cannot assign order with id " + orderId + " because it is in " + currentStatus + " status. Only PENDING orders can be assigned.");
    }
}
