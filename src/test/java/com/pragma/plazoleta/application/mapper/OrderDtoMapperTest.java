package com.pragma.plazoleta.application.mapper;

import com.pragma.plazoleta.application.dto.request.OrderItemRequestDto;
import com.pragma.plazoleta.application.dto.response.OrderItemResponseDto;
import com.pragma.plazoleta.application.dto.response.OrderResponseDto;
import com.pragma.plazoleta.domain.model.Order;
import com.pragma.plazoleta.domain.model.OrderItem;
import com.pragma.plazoleta.domain.model.OrderStatus;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class OrderDtoMapperTest {

    private final OrderDtoMapper mapper = Mappers.getMapper(OrderDtoMapper.class);

    @Test
    void toOrderItem_shouldMapFields() {
        OrderItemRequestDto request = OrderItemRequestDto.builder()
                .dishId(2L)
                .quantity(3)
                .build();

        OrderItem item = mapper.toOrderItem(request);

        assertThat(item).isNotNull();
        assertThat(item.getDishId()).isEqualTo(request.getDishId());
        assertThat(item.getQuantity()).isEqualTo(request.getQuantity());
    }

    @Test
    void toOrderItemList_shouldMapList() {
        OrderItemRequestDto r1 = OrderItemRequestDto.builder().dishId(1L).quantity(1).build();
        OrderItemRequestDto r2 = OrderItemRequestDto.builder().dishId(2L).quantity(2).build();

        List<OrderItem> list = mapper.toOrderItemList(List.of(r1, r2));

        assertThat(list).hasSize(2);
        assertThat(list.get(0).getDishId()).isEqualTo(1L);
        assertThat(list.get(1).getDishId()).isEqualTo(2L);
    }

    @Test
    void toOrderResponseDto_shouldMapStatus() {
        Order order = new Order();
        order.setStatus(OrderStatus.IN_PREPARATION);

        OrderResponseDto dto = mapper.toOrderResponseDto(order);

        assertThat(dto).isNotNull();
        assertThat(dto.getStatus()).isEqualTo(OrderStatus.IN_PREPARATION.name());
    }

    @Test
    void toOrderItemResponseDto_shouldMapFields() {
        OrderItem item = new OrderItem(5L, 4);

        OrderItemResponseDto dto = mapper.toOrderItemResponseDto(item);

        assertThat(dto).isNotNull();
        assertThat(dto.getQuantity()).isEqualTo(item.getQuantity());
        // dishName and dishPrice are ignored in mapping configuration, so should be null
        assertThat(dto.getDishName()).isNull();
        assertThat(dto.getDishPrice()).isNull();
    }

    @Test
    void toOrderItemResponseDtoList_shouldMapList() {
        List<OrderItem> items = List.of(new OrderItem(1L,1), new OrderItem(2L,2));

        var list = mapper.toOrderItemResponseDtoList(items);

        assertThat(list).hasSize(2);
        assertThat(list.get(0).getQuantity()).isEqualTo(1);
        assertThat(list.get(1).getQuantity()).isEqualTo(2);
    }

    @Test
    void toOrderItem_shouldReturnNull_whenRequestIsNull() {
        assertThat(mapper.toOrderItem(null)).isNull();
    }

    @Test
    void toOrderItemList_shouldReturnNull_whenListIsNull() {
        assertThat(mapper.toOrderItemList(null)).isNull();
    }

    @Test
    void toOrderResponseDto_shouldReturnNull_whenOrderIsNull() {
        assertThat(mapper.toOrderResponseDto(null)).isNull();
    }

    @Test
    void toOrderItemResponseDto_shouldReturnNull_whenItemIsNull() {
        assertThat(mapper.toOrderItemResponseDto(null)).isNull();
    }

    @Test
    void toOrderItemResponseDtoList_shouldReturnNull_whenListIsNull() {
        assertThat(mapper.toOrderItemResponseDtoList(null)).isNull();
    }
}
