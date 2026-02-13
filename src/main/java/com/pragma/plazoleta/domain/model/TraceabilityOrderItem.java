package com.pragma.plazoleta.domain.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TraceabilityOrderItem {
    private Long dishId;
    private String dishName;
    private Integer quantity;
    private Long unitPrice;
    private Long linePrice;
    private String category;
}
