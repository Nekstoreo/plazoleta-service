package com.pragma.plazoleta.application.handler;

import com.pragma.plazoleta.application.dto.request.AssignOrderRequestDto;
import com.pragma.plazoleta.application.dto.request.CreateOrderRequestDto;
import com.pragma.plazoleta.application.dto.request.MarkOrderReadyRequestDto;
import com.pragma.plazoleta.application.dto.response.OrderItemResponseDto;
import com.pragma.plazoleta.application.dto.response.OrderResponseDto;
import com.pragma.plazoleta.application.dto.response.PagedResponse;
import com.pragma.plazoleta.application.mapper.OrderDtoMapper;
import com.pragma.plazoleta.domain.api.IOrderServicePort;
import com.pragma.plazoleta.domain.model.Order;
import com.pragma.plazoleta.domain.model.OrderItem;
import com.pragma.plazoleta.domain.model.OrderStatus;
import com.pragma.plazoleta.domain.model.PagedResult;
import com.pragma.plazoleta.domain.spi.IDishPersistencePort;
import com.pragma.plazoleta.domain.spi.IRestaurantPersistencePort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<OrderResponseDto> getOrdersByStatus(Long employeeId, String status, int page, int size) {
        OrderStatus orderStatus = OrderStatus.valueOf(status.toUpperCase());

        PagedResult<Order> pagedResult = orderServicePort.getOrdersByRestaurantAndStatus(
                employeeId, orderStatus, page, size);

        List<OrderResponseDto> orderResponses = pagedResult.getContent().stream()
                .map(this::buildOrderResponse)
                .collect(Collectors.toList());

        return PagedResponse.<OrderResponseDto>builder()
                .content(orderResponses)
                .page(pagedResult.getPage())
                .size(pagedResult.getSize())
                .totalElements(pagedResult.getTotalElements())
                .totalPages(pagedResult.getTotalPages())
                .first(pagedResult.isFirst())
                .last(pagedResult.isLast())
                .build();
    }

    @Override
    public OrderResponseDto assignOrderToEmployee(AssignOrderRequestDto request, Long employeeId) {
        Order assignedOrder = orderServicePort.assignOrderToEmployee(request.getOrderId(), employeeId);
        return buildOrderResponse(assignedOrder);
    }

    @Override
    public OrderResponseDto markOrderAsReady(MarkOrderReadyRequestDto request, Long employeeId) {
        Order readyOrder = orderServicePort.markOrderAsReady(request.getOrderId(), employeeId);
        return buildOrderResponse(readyOrder);
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
