package com.pragma.plazoleta.domain.exception;

public class UserNotRestaurantOwnerException extends RuntimeException {

    public UserNotRestaurantOwnerException(Long userId, Long restaurantId) {
        super("User with id " + userId + " is not the owner of restaurant with id " + restaurantId);
    }

    public UserNotRestaurantOwnerException(String message) {
        super(message);
    }
}
