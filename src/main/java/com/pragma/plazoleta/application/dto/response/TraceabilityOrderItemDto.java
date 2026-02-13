package com.pragma.plazoleta.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TraceabilityOrderItemDto {
    private Long dishId;
    private String dishName;
    private Integer quantity;
    private Long unitPrice;
    private Long linePrice;
    private String category;
}
