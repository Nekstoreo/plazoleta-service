package com.pragma.plazoleta.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Order item information")
public class OrderItemResponseDto {

    @Schema(description = "Order item ID", example = "1")
    private Long id;

    @Schema(description = "Dish ID", example = "5")
    private Long dishId;

    @Schema(description = "Dish name", example = "Hamburguesa Clásica")
    private String dishName;

    @Schema(description = "Dish unit price", example = "25000")
    private Integer dishPrice;

    @Schema(description = "Ordered quantity", example = "2")
    private Integer quantity;
}
