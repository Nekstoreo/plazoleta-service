package com.pragma.plazoleta.domain.usecase;

import com.pragma.plazoleta.domain.api.IOrderServicePort;
import com.pragma.plazoleta.domain.exception.ClientHasActiveOrderException;
import com.pragma.plazoleta.domain.exception.DishNotActiveException;
import com.pragma.plazoleta.domain.exception.DishNotFoundException;
import com.pragma.plazoleta.domain.exception.DishNotFromRestaurantException;
import com.pragma.plazoleta.domain.exception.EmptyOrderException;
import com.pragma.plazoleta.domain.exception.EmployeeNotAssociatedWithRestaurantException;
import com.pragma.plazoleta.domain.exception.InvalidQuantityException;
import com.pragma.plazoleta.domain.exception.RestaurantNotFoundException;
import com.pragma.plazoleta.domain.model.Dish;
import com.pragma.plazoleta.domain.model.Order;
import com.pragma.plazoleta.domain.model.OrderItem;
import com.pragma.plazoleta.domain.model.OrderStatus;
import com.pragma.plazoleta.domain.model.PagedResult;
import com.pragma.plazoleta.domain.spi.IDishPersistencePort;
import com.pragma.plazoleta.domain.spi.IEmployeeRestaurantPort;
import com.pragma.plazoleta.domain.spi.IOrderPersistencePort;
import com.pragma.plazoleta.domain.spi.IRestaurantPersistencePort;

import java.time.LocalDateTime;
import java.util.List;

public class OrderUseCase implements IOrderServicePort {

    private final IOrderPersistencePort orderPersistencePort;
    private final IRestaurantPersistencePort restaurantPersistencePort;
    private final IDishPersistencePort dishPersistencePort;
    private final IEmployeeRestaurantPort employeeRestaurantPort;

    public OrderUseCase(IOrderPersistencePort orderPersistencePort,
                        IRestaurantPersistencePort restaurantPersistencePort,
                        IDishPersistencePort dishPersistencePort,
                        IEmployeeRestaurantPort employeeRestaurantPort) {
        this.orderPersistencePort = orderPersistencePort;
        this.restaurantPersistencePort = restaurantPersistencePort;
        this.dishPersistencePort = dishPersistencePort;
        this.employeeRestaurantPort = employeeRestaurantPort;
    }

    @Override
    public Order createOrder(Order order) {
        validateOrderNotEmpty(order.getItems());
        validateRestaurantExists(order.getRestaurantId());
        validateClientHasNoActiveOrder(order.getClientId());
        validateOrderItems(order.getItems(), order.getRestaurantId());

        order.setStatus(OrderStatus.PENDING);
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());

        return orderPersistencePort.saveOrder(order);
    }

    @Override
    public PagedResult<Order> getOrdersByRestaurantAndStatus(Long employeeId, OrderStatus status, int page, int size) {
        Long restaurantId = getEmployeeRestaurantId(employeeId);
        return orderPersistencePort.findByRestaurantIdAndStatusPaginated(restaurantId, status, page, size);
    }

    private Long getEmployeeRestaurantId(Long employeeId) {
        return employeeRestaurantPort.getRestaurantIdByEmployeeId(employeeId)
                .orElseThrow(() -> new EmployeeNotAssociatedWithRestaurantException(employeeId));
    }

    private void validateOrderNotEmpty(List<OrderItem> items) {
        if (items == null || items.isEmpty()) {
            throw new EmptyOrderException();
        }
    }

    private void validateRestaurantExists(Long restaurantId) {
        restaurantPersistencePort.findById(restaurantId)
                .orElseThrow(() -> new RestaurantNotFoundException(restaurantId));
    }

    private void validateClientHasNoActiveOrder(Long clientId) {
        if (orderPersistencePort.existsActiveOrderByClientId(clientId)) {
            throw new ClientHasActiveOrderException(clientId);
        }
    }

    private void validateOrderItems(List<OrderItem> items, Long restaurantId) {
        for (OrderItem item : items) {
            validateQuantity(item.getQuantity());
            validateDish(item.getDishId(), restaurantId);
        }
    }

    private void validateQuantity(Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new InvalidQuantityException();
        }
    }

    private void validateDish(Long dishId, Long restaurantId) {
        Dish dish = dishPersistencePort.findById(dishId)
                .orElseThrow(() -> new DishNotFoundException(dishId));

        if (!dish.getRestaurantId().equals(restaurantId)) {
            throw new DishNotFromRestaurantException(dishId, restaurantId);
        }

        if (dish.getActive() == null || !dish.getActive()) {
            throw new DishNotActiveException(dishId);
        }
    }
}
