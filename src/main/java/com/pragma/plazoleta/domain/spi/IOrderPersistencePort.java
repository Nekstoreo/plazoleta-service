package com.pragma.plazoleta.domain.spi;

import com.pragma.plazoleta.domain.model.Order;
import com.pragma.plazoleta.domain.model.OrderStatus;
import com.pragma.plazoleta.domain.model.PagedResult;

public interface IOrderPersistencePort {

    Order saveOrder(Order order);

    boolean existsActiveOrderByClientId(Long clientId);

    PagedResult<Order> findByRestaurantIdAndStatusPaginated(Long restaurantId, OrderStatus status, int page, int size);
}
