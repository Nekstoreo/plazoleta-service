package com.pragma.plazoleta.application.handler;

import com.pragma.plazoleta.application.dto.request.AssignOrderRequestDto;
import com.pragma.plazoleta.application.dto.request.CreateOrderRequestDto;
import com.pragma.plazoleta.application.dto.request.DeliverOrderRequestDto;
import com.pragma.plazoleta.application.dto.request.MarkOrderReadyRequestDto;
import com.pragma.plazoleta.application.dto.response.OrderResponseDto;
import com.pragma.plazoleta.application.dto.response.PagedResponse;

import com.pragma.plazoleta.application.dto.response.TraceabilityResponseDto;
import java.util.List;

public interface IOrderHandler {

    OrderResponseDto createOrder(CreateOrderRequestDto request, Long clientId);

    PagedResponse<OrderResponseDto> getOrdersByStatus(Long employeeId, String status, int page, int size);

    OrderResponseDto assignOrderToEmployee(AssignOrderRequestDto request, Long employeeId);

    OrderResponseDto markOrderAsReady(MarkOrderReadyRequestDto request, Long employeeId);

    OrderResponseDto deliverOrder(DeliverOrderRequestDto request, Long employeeId);

    void cancelOrder(Long orderId, Long clientId);

    List<TraceabilityResponseDto> getTraceabilityByOrderId(Long orderId, Long clientId);
}

