package com.pragma.plazoleta.domain.exception;

public class InvalidOrderStatusException extends RuntimeException {

    public InvalidOrderStatusException(String message) {
        super(message);
    }

    public InvalidOrderStatusException(Long orderId, String currentStatus) {
        super("Order with id " + orderId + " is in invalid status: " + currentStatus);
    }
}
