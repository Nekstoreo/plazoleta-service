package com.pragma.plazoleta.application.handler;

import com.pragma.plazoleta.application.dto.request.CreateOrderRequestDto;
import com.pragma.plazoleta.application.dto.response.OrderItemResponseDto;
import com.pragma.plazoleta.application.dto.response.OrderResponseDto;
import com.pragma.plazoleta.application.mapper.OrderDtoMapper;
import com.pragma.plazoleta.domain.api.IOrderServicePort;
import com.pragma.plazoleta.domain.model.Order;
import com.pragma.plazoleta.domain.model.OrderItem;
import com.pragma.plazoleta.domain.spi.IDishPersistencePort;
import com.pragma.plazoleta.domain.spi.IRestaurantPersistencePort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class OrderHandler implements IOrderHandler {

    private final IOrderServicePort orderServicePort;
    private final IRestaurantPersistencePort restaurantPersistencePort;
    private final IDishPersistencePort dishPersistencePort;
    private final OrderDtoMapper orderDtoMapper;

    public OrderHandler(IOrderServicePort orderServicePort,
                        IRestaurantPersistencePort restaurantPersistencePort,
                        IDishPersistencePort dishPersistencePort,
                        OrderDtoMapper orderDtoMapper) {
        this.orderServicePort = orderServicePort;
        this.restaurantPersistencePort = restaurantPersistencePort;
        this.dishPersistencePort = dishPersistencePort;
        this.orderDtoMapper = orderDtoMapper;
    }

    @Override
    public OrderResponseDto createOrder(CreateOrderRequestDto request, Long clientId) {
        Order order = orderDtoMapper.toOrder(request);
        order.setClientId(clientId);
        order.setItems(orderDtoMapper.toOrderItemList(request.getItems()));

        Order createdOrder = orderServicePort.createOrder(order);

        return buildOrderResponse(createdOrder);
    }

    private OrderResponseDto buildOrderResponse(Order order) {
        OrderResponseDto response = orderDtoMapper.toOrderResponseDto(order);

        restaurantPersistencePort.findById(order.getRestaurantId())
                .ifPresent(restaurant -> response.setRestaurantName(restaurant.getName()));

        List<OrderItemResponseDto> itemResponses = new ArrayList<>();
        for (OrderItem item : order.getItems()) {
            OrderItemResponseDto itemResponse = orderDtoMapper.toOrderItemResponseDto(item);

            dishPersistencePort.findById(item.getDishId())
                    .ifPresent(dish -> {
                        itemResponse.setDishName(dish.getName());
                        itemResponse.setDishPrice(dish.getPrice());
                    });

            itemResponses.add(itemResponse);
        }

        response.setItems(itemResponses);

        return response;
    }
}
