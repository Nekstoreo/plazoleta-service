package com.pragma.plazoleta.domain.spi;

public interface INotificationPort {

    void sendOrderReadyNotification(String phoneNumber, String orderId, String securityPin, String restaurantName);
}
