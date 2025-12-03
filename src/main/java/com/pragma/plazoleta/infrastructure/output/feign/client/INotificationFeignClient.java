package com.pragma.plazoleta.infrastructure.output.feign.client;

import com.pragma.plazoleta.infrastructure.output.feign.dto.NotificationResponseDto;
import com.pragma.plazoleta.infrastructure.output.feign.dto.OrderReadyNotificationRequestDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "mensajeria-service", url = "${microservices.mensajeria.url}")
public interface INotificationFeignClient {

    @PostMapping("/api/v1/notifications/order-ready")
    NotificationResponseDto sendOrderReadyNotification(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody OrderReadyNotificationRequestDto request
    );
}
