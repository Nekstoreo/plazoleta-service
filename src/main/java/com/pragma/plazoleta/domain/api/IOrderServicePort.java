package com.pragma.plazoleta.domain.api;

import com.pragma.plazoleta.domain.model.Order;
import com.pragma.plazoleta.domain.model.OrderStatus;
import com.pragma.plazoleta.domain.model.PagedResult;

import com.pragma.plazoleta.domain.model.Traceability;
import java.util.List;

public interface IOrderServicePort {

    Order createOrder(Order order);

    PagedResult<Order> getOrdersByRestaurantAndStatus(Long employeeId, OrderStatus status, int page, int size);

    Order assignOrderToEmployee(Long orderId, Long employeeId);

    Order markOrderAsReady(Long orderId, Long employeeId);

    Order markOrderAsDelivered(Long orderId, Long employeeId, String securityPin);

    void cancelOrder(Long orderId, Long clientId);

    List<Traceability> getTraceabilityByOrderId(Long orderId, Long clientId);
}
