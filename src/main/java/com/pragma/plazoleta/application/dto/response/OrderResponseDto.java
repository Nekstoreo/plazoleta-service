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
@Schema(description = "Order information")
public class OrderResponseDto {

    @Schema(description = "Order ID", example = "1")
    private Long id;

    @Schema(description = "Client ID", example = "10")
    private Long clientId;

    @Schema(description = "Restaurant ID", example = "3")
    private Long restaurantId;

    @Schema(description = "Restaurant name", example = "Restaurante La Esquina")
    private String restaurantName;

    @Schema(description = "Assigned employee ID", example = "50")
    private Long employeeId;

    @Schema(description = "Current status of the order", example = "PENDING")
    private String status;

    @Schema(description = "Order creation timestamp", example = "2025-12-01T14:30:00")
    private LocalDateTime createdAt;

    @Schema(description = "Last update timestamp", example = "2025-12-01T14:30:00")
    private LocalDateTime updatedAt;

    @Schema(description = "Security PIN for order pickup", example = "123456")
    private String securityPin;

    @Schema(description = "List of order items")
    private List<OrderItemResponseDto> items;
}
