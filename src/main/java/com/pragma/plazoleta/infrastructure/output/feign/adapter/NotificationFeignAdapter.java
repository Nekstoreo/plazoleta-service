package com.pragma.plazoleta.infrastructure.output.feign.adapter;

import com.pragma.plazoleta.domain.spi.INotificationPort;
import com.pragma.plazoleta.infrastructure.output.feign.client.INotificationFeignClient;
import com.pragma.plazoleta.infrastructure.output.feign.dto.OrderReadyNotificationRequestDto;
import feign.FeignException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class NotificationFeignAdapter implements INotificationPort {

    private static final Logger logger = LoggerFactory.getLogger(NotificationFeignAdapter.class);
    private static final String AUTHORIZATION_HEADER = "Authorization";

    private final INotificationFeignClient notificationFeignClient;

    public NotificationFeignAdapter(INotificationFeignClient notificationFeignClient) {
        this.notificationFeignClient = notificationFeignClient;
    }

    @Override
    public void sendOrderReadyNotification(String phoneNumber, String orderId, String securityPin, String restaurantName) {
        try {
            String authToken = getAuthorizationHeader();

            OrderReadyNotificationRequestDto request = new OrderReadyNotificationRequestDto(
                    phoneNumber,
                    orderId,
                    securityPin,
                    restaurantName
            );

            var response = notificationFeignClient.sendOrderReadyNotification(authToken, request);

            if (response.isSuccess()) {
                logger.info("Order ready notification sent successfully for order {}. MessageId: {}",
                        orderId, response.getMessageId());
            } else {
                logger.warn("Failed to send order ready notification for order {}: {}",
                        orderId, response.getMessage());
            }
        } catch (FeignException e) {
            logger.error("Error calling notification service for order {}: {}", orderId, e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Unexpected error sending notification for order {}: {}", orderId, e.getMessage(), e);
        }
    }

    private String getAuthorizationHeader() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            return request.getHeader(AUTHORIZATION_HEADER);
        }
        return null;
    }
}
