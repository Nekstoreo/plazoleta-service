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
@Schema(description = "Solicitud para crear un nuevo pedido")
public class CreateOrderRequestDto {

    @NotNull(message = "El ID del restaurante es obligatorio")
    @Schema(description = "ID del restaurante donde se realiza el pedido", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long restaurantId;

    @NotEmpty(message = "El pedido debe contener al menos un plato")
    @Valid
    @Schema(description = "Lista de platos con sus cantidades", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<OrderItemRequestDto> items;
}
