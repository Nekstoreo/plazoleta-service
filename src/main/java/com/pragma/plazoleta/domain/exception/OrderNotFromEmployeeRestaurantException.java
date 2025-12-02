package com.pragma.plazoleta.domain.exception;

public class OrderNotFromEmployeeRestaurantException extends RuntimeException {

    public OrderNotFromEmployeeRestaurantException(Long orderId, Long restaurantId) {
        super("Order with id " + orderId + " does not belong to restaurant with id " + restaurantId);
    }
}
