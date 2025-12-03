package com.pragma.plazoleta.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to mark an order as delivered")
public class DeliverOrderRequestDto {

    @NotNull(message = "Order ID is required")
    @Schema(description = "ID of the order to mark as delivered", example = "1")
    private Long orderId;

    @NotBlank(message = "Security PIN is required")
    @Schema(description = "Security PIN sent to the client", example = "123456")
    private String securityPin;
}
