package com.pragma.plazoleta.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {

    private Long id;
    private Long orderId;
    private Long dishId;
    private Integer quantity;

    public OrderItem(Long dishId, Integer quantity) {
        this.dishId = dishId;
        this.quantity = quantity;
    }
}
