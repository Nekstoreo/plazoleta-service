package com.pragma.plazoleta.application.handler;

import com.pragma.plazoleta.application.dto.request.CreateOrderRequestDto;
import com.pragma.plazoleta.application.dto.response.OrderResponseDto;
import com.pragma.plazoleta.application.dto.response.PagedResponse;

public interface IOrderHandler {

    OrderResponseDto createOrder(CreateOrderRequestDto request, Long clientId);

    PagedResponse<OrderResponseDto> getOrdersByStatus(Long employeeId, String status, int page, int size);
}
