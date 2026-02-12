package com.pragma.plazoleta.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to create a new order")
public class CreateOrderRequestDto {

    @NotNull(message = "Restaurant ID is required")
    @Schema(description = "ID of the restaurant where the order is placed", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long restaurantId;

    @NotEmpty(message = "The order must contain at least one dish")
    @Valid
    @Schema(description = "List of dishes with their quantities", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<OrderItemRequestDto> items;
}
