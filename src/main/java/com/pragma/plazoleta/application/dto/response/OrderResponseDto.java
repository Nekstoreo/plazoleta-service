package com.pragma.plazoleta.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Información del pedido creado")
public class OrderResponseDto {

    @Schema(description = "ID del pedido", example = "1")
    private Long id;

    @Schema(description = "ID del cliente", example = "10")
    private Long clientId;

    @Schema(description = "ID del restaurante", example = "3")
    private Long restaurantId;

    @Schema(description = "Nombre del restaurante", example = "Restaurante La Esquina")
    private String restaurantName;

    @Schema(description = "Estado actual del pedido", example = "PENDING")
    private String status;

    @Schema(description = "Fecha y hora de creación del pedido", example = "2025-12-01T14:30:00")
    private LocalDateTime createdAt;

    @Schema(description = "Fecha y hora de última actualización", example = "2025-12-01T14:30:00")
    private LocalDateTime updatedAt;

    @Schema(description = "Lista de platos del pedido")
    private List<OrderItemResponseDto> items;
}
