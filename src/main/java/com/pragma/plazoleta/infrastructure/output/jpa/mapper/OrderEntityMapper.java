package com.pragma.plazoleta.infrastructure.output.jpa.mapper;

import com.pragma.plazoleta.domain.model.Order;
import com.pragma.plazoleta.domain.model.OrderItem;
import com.pragma.plazoleta.domain.model.OrderStatus;
import com.pragma.plazoleta.infrastructure.output.jpa.entity.OrderEntity;
import com.pragma.plazoleta.infrastructure.output.jpa.entity.OrderItemEntity;
import com.pragma.plazoleta.infrastructure.output.jpa.entity.OrderStatusEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface OrderEntityMapper {

    @Mapping(target = "restaurant.id", source = "restaurantId")
    @Mapping(target = "status", source = "status", qualifiedByName = "toStatusEntity")
    @Mapping(target = "items", ignore = true)
    OrderEntity toEntity(Order order);

    @Mapping(target = "restaurantId", source = "restaurant.id")
    @Mapping(target = "status", source = "status", qualifiedByName = "toStatusDomain")
    @Mapping(target = "items", source = "items")
    Order toDomain(OrderEntity entity);

    @Mapping(target = "order", ignore = true)
    @Mapping(target = "dish.id", source = "dishId")
    OrderItemEntity toItemEntity(OrderItem orderItem);

    @Mapping(target = "orderId", source = "order.id")
    @Mapping(target = "dishId", source = "dish.id")
    OrderItem toItemDomain(OrderItemEntity entity);

    List<OrderItem> toItemDomainList(List<OrderItemEntity> entities);

    @Named("toStatusEntity")
    default OrderStatusEntity toStatusEntity(OrderStatus status) {
        if (status == null) {
            return null;
        }
        return OrderStatusEntity.valueOf(status.name());
    }

    @Named("toStatusDomain")
    default OrderStatus toStatusDomain(OrderStatusEntity status) {
        if (status == null) {
            return null;
        }
        return OrderStatus.valueOf(status.name());
    }
}
