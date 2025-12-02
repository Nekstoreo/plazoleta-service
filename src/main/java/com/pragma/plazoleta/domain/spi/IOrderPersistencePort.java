package com.pragma.plazoleta.domain.spi;

import com.pragma.plazoleta.domain.model.Order;

public interface IOrderPersistencePort {

    Order saveOrder(Order order);

    boolean existsActiveOrderByClientId(Long clientId);
}
