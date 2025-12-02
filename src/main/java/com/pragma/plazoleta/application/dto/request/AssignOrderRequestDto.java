package com.pragma.plazoleta.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Solicitud para asignar un pedido a un empleado y cambiar su estado")
public class AssignOrderRequestDto {

    @NotNull(message = "El ID del pedido es obligatorio")
    @Schema(description = "ID del pedido a asignar", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long orderId;
}
