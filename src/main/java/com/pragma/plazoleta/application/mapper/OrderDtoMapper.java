package com.pragma.plazoleta.application.mapper;

import com.pragma.plazoleta.application.dto.request.CreateOrderRequestDto;
import com.pragma.plazoleta.application.dto.request.OrderItemRequestDto;
import com.pragma.plazoleta.application.dto.response.OrderItemResponseDto;
import com.pragma.plazoleta.application.dto.response.OrderResponseDto;
import com.pragma.plazoleta.domain.model.Order;
import com.pragma.plazoleta.domain.model.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface OrderDtoMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "clientId", ignore = true)
    @Mapping(target = "employeeId", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "securityPin", ignore = true)
    Order toOrder(CreateOrderRequestDto request);

    OrderItem toOrderItem(OrderItemRequestDto itemRequest);

    List<OrderItem> toOrderItemList(List<OrderItemRequestDto> itemRequests);

    @Mapping(target = "status", expression = "java(order.getStatus().name())")
    @Mapping(target = "restaurantName", ignore = true)
    OrderResponseDto toOrderResponseDto(Order order);

    @Mapping(target = "dishName", ignore = true)
    @Mapping(target = "dishPrice", ignore = true)
    OrderItemResponseDto toOrderItemResponseDto(OrderItem orderItem);

    List<OrderItemResponseDto> toOrderItemResponseDtoList(List<OrderItem> orderItems);
}
