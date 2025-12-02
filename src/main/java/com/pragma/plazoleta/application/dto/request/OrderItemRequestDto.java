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
@Schema(description = "Representa un item del pedido con el plato y la cantidad")
public class OrderItemRequestDto {

    @NotNull(message = "El ID del plato es obligatorio")
    @Schema(description = "ID del plato a ordenar", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long dishId;

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser mayor a 0")
    @Schema(description = "Cantidad del plato a ordenar", example = "2", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer quantity;
}
