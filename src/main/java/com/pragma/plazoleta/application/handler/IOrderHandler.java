package com.pragma.plazoleta.application.handler;

import com.pragma.plazoleta.application.dto.request.CreateOrderRequestDto;
import com.pragma.plazoleta.application.dto.response.OrderResponseDto;

public interface IOrderHandler {

    OrderResponseDto createOrder(CreateOrderRequestDto request, Long clientId);
}
