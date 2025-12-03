package com.pragma.plazoleta.domain.api;

import com.pragma.plazoleta.domain.model.Order;
import com.pragma.plazoleta.domain.model.OrderStatus;
import com.pragma.plazoleta.domain.model.PagedResult;

public interface IOrderServicePort {

    Order createOrder(Order order);

    PagedResult<Order> getOrdersByRestaurantAndStatus(Long employeeId, OrderStatus status, int page, int size);

    Order assignOrderToEmployee(Long orderId, Long employeeId);

    Order markOrderAsReady(Long orderId, Long employeeId);
}
