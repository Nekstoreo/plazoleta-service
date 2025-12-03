package com.pragma.plazoleta.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to mark an order as ready")
public class MarkOrderReadyRequestDto {

    @NotNull(message = "Order ID is required")
    @Schema(description = "ID of the order to mark as ready", example = "1")
    private Long orderId;
}
