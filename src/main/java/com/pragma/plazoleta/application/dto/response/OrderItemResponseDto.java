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
@Schema(description = "Información de un item del pedido")
public class OrderItemResponseDto {

    @Schema(description = "ID del item del pedido", example = "1")
    private Long id;

    @Schema(description = "ID del plato", example = "5")
    private Long dishId;

    @Schema(description = "Nombre del plato", example = "Hamburguesa Clásica")
    private String dishName;

    @Schema(description = "Precio unitario del plato", example = "25000")
    private Integer dishPrice;

    @Schema(description = "Cantidad ordenada", example = "2")
    private Integer quantity;
}
