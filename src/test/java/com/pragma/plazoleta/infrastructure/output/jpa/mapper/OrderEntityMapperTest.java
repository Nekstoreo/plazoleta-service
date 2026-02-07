package com.pragma.plazoleta.infrastructure.output.jpa.mapper;

import com.pragma.plazoleta.domain.model.OrderItem;
import com.pragma.plazoleta.domain.model.OrderStatus;
import com.pragma.plazoleta.infrastructure.output.jpa.entity.DishEntity;
import com.pragma.plazoleta.infrastructure.output.jpa.entity.OrderEntity;
import com.pragma.plazoleta.infrastructure.output.jpa.entity.OrderItemEntity;
import com.pragma.plazoleta.infrastructure.output.jpa.entity.OrderStatusEntity;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;

class OrderEntityMapperTest {

    private final OrderEntityMapper mapper = Mappers.getMapper(OrderEntityMapper.class);

    @Test
    void toStatusEntity_and_toStatusDomain_shouldConvert() {
        OrderStatusEntity ent = mapper.toStatusEntity(OrderStatus.PENDING);
        assertThat(ent).isEqualTo(OrderStatusEntity.PENDING);

        OrderStatus dom = mapper.toStatusDomain(OrderStatusEntity.DELIVERED);
        assertThat(dom).isEqualTo(OrderStatus.DELIVERED);
    }

    @Test
    void toItemEntity_shouldMapDishId() {
        OrderItem item = new OrderItem(33L, 2);

        OrderItemEntity e = mapper.toItemEntity(item);

        assertThat(e).isNotNull();
        assertThat(e.getDish()).isNotNull();
        assertThat(e.getDish().getId()).isEqualTo(33L);
        assertThat(e.getQuantity()).isEqualTo(2);
    }

    @Test
    void toItemDomain_shouldMapIds() {
        OrderItemEntity e = new OrderItemEntity();
        e.setQuantity(4);
        e.setDish(new DishEntity());
        e.getDish().setId(77L);
        OrderEntity oe = new OrderEntity();
        oe.setId(19L);
        e.setOrder(oe);

        OrderItem item = mapper.toItemDomain(e);

        assertThat(item).isNotNull();
        assertThat(item.getDishId()).isEqualTo(77L);
        assertThat(item.getOrderId()).isEqualTo(19L);
        assertThat(item.getQuantity()).isEqualTo(4);
    }
}
