package com.pragma.plazoleta.infrastructure.output.feign.adapter;

import com.pragma.plazoleta.infrastructure.output.feign.client.INotificationFeignClient;
import com.pragma.plazoleta.infrastructure.output.feign.dto.NotificationResponseDto;
import feign.FeignException;
import feign.Request;
import feign.RequestTemplate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationFeignAdapterTest {

    @Mock
    private INotificationFeignClient notificationFeignClient;

    @InjectMocks
    private NotificationFeignAdapter adapter;

    @AfterEach
    void clearRequestContext() {
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    void sendOrderReadyNotification_ShouldSendWithAuthorizationHeader() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer test-token");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        NotificationResponseDto response = new NotificationResponseDto(true, "MSG-1", "ok");
        when(notificationFeignClient.sendOrderReadyNotification(anyString(), any())).thenReturn(response);

        adapter.sendOrderReadyNotification("+573001234567", "100", "123456", "Restaurante Uno");

        ArgumentCaptor<String> tokenCaptor = ArgumentCaptor.forClass(String.class);
        verify(notificationFeignClient).sendOrderReadyNotification(tokenCaptor.capture(), any());
        assertEquals("Bearer test-token", tokenCaptor.getValue());
    }

    @Test
    void sendOrderReadyNotification_ShouldSendWithNullTokenWhenNoRequestContext() {
        NotificationResponseDto response = new NotificationResponseDto(false, null, "error");
        when(notificationFeignClient.sendOrderReadyNotification(any(), any())).thenReturn(response);

        adapter.sendOrderReadyNotification("+573001234567", "101", "654321", "Restaurante Dos");

        ArgumentCaptor<String> tokenCaptor = ArgumentCaptor.forClass(String.class);
        verify(notificationFeignClient).sendOrderReadyNotification(tokenCaptor.capture(), any());
        assertNull(tokenCaptor.getValue());
    }

    @Test
    void sendOrderReadyNotification_ShouldHandleFeignException() {
        when(notificationFeignClient.sendOrderReadyNotification(any(), any())).thenThrow(notFoundException());

        adapter.sendOrderReadyNotification("+573001234567", "102", "111111", "Restaurante Tres");

        verify(notificationFeignClient).sendOrderReadyNotification(any(), any());
    }

    private FeignException notFoundException() {
        Request request = Request.create(
                Request.HttpMethod.POST,
                "/api/v1/notifications/order-ready",
                Collections.emptyMap(),
                null,
                new RequestTemplate()
        );
        return new FeignException.NotFound("not found", request, null, null);
    }
}
