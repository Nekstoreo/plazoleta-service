package com.pragma.plazoleta.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
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
@Schema(description = "Represents an order item with the dish and quantity")
public class OrderItemRequestDto {

    @NotNull(message = "Dish ID is required")
    @Schema(description = "ID of the dish to order", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long dishId;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be greater than 0")
    @Schema(description = "Quantity of the dish to order", example = "2", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer quantity;
}
