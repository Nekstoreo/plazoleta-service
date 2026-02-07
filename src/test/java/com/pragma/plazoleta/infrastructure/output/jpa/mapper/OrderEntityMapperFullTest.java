package com.pragma.plazoleta.infrastructure.output.jpa.mapper;

import com.pragma.plazoleta.domain.model.Order;
import com.pragma.plazoleta.domain.model.OrderStatus;
import com.pragma.plazoleta.infrastructure.output.jpa.entity.DishEntity;
import com.pragma.plazoleta.infrastructure.output.jpa.entity.OrderEntity;
import com.pragma.plazoleta.infrastructure.output.jpa.entity.OrderItemEntity;
import com.pragma.plazoleta.infrastructure.output.jpa.entity.OrderStatusEntity;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class OrderEntityMapperFullTest {

    private final OrderEntityMapper mapper = Mappers.getMapper(OrderEntityMapper.class);

    @Test
    void toEntity_shouldMapStatusAndRestaurant() {
        Order o = new Order();
        o.setRestaurantId(77L);
        o.setStatus(OrderStatus.IN_PREPARATION);

        OrderEntity e = mapper.toEntity(o);

        assertThat(e).isNotNull();
        assertThat(e.getRestaurant()).isNotNull();
        assertThat(e.getRestaurant().getId()).isEqualTo(77L);
        assertThat(e.getStatus()).isEqualTo(OrderStatusEntity.IN_PREPARATION);
    }

    @Test
    void toDomain_shouldMapItemsAndStatus() {
        OrderEntity e = new OrderEntity();
        e.setId(5L);
        e.setStatus(OrderStatusEntity.READY);
        var orderItem = new OrderItemEntity();
        orderItem.setQuantity(3);
        var dish = new DishEntity();
        dish.setId(9L);
        orderItem.setDish(dish);
        var parent = new OrderEntity();
        parent.setId(5L);
        orderItem.setOrder(parent);

        e.setItems(List.of(orderItem));
        var r = mapper.toDomain(e);

        assertThat(r).isNotNull();
        assertThat(r.getStatus()).isEqualTo(OrderStatus.READY);
        assertThat(r.getItems()).hasSize(1);
        assertThat(r.getItems().get(0).getDishId()).isEqualTo(9L);
        assertThat(r.getItems().get(0).getOrderId()).isEqualTo(5L);
    }

    @Test
    void toDomain_shouldMapRestaurantId_whenRestaurantEntityPresent() {
        OrderEntity e = new OrderEntity();
        var r = new com.pragma.plazoleta.infrastructure.output.jpa.entity.RestaurantEntity();
        r.setId(222L);
        e.setRestaurant(r);

        var o = mapper.toDomain(e);
        assertThat(o.getRestaurantId()).isEqualTo(222L);
    }

    @Test
    void toItemDomainList_shouldHandleEmptyAndList() {
        assertThat(mapper.toItemDomainList(List.of())).isEmpty();

        OrderItemEntity e1 = new OrderItemEntity();
        e1.setDish(new DishEntity());
        e1.getDish().setId(1L);
        e1.setOrder(new OrderEntity());
        e1.getOrder().setId(2L);
        e1.setQuantity(4);

        var list = mapper.toItemDomainList(List.of(e1));
        assertThat(list).hasSize(1);
        assertThat(list.get(0).getDishId()).isEqualTo(1L);
    }
}
