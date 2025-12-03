package com.pragma.plazoleta.infrastructure.output.feign.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderReadyNotificationRequestDto {

    private String phoneNumber;
    private String orderId;
    private String securityPin;
    private String restaurantName;
}
