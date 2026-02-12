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
@Schema(description = "Request to assign an order to an employee and change its status")
public class AssignOrderRequestDto {

    @NotNull(message = "Order ID is required")
    @Schema(description = "ID of the order to assign", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long orderId;
}
