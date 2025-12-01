package com.pragma.plazoleta.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateRestaurantRequest(
        @NotBlank(message = "Name is required")
        String name,

        @NotBlank(message = "NIT is required")
        String nit,

        @NotBlank(message = "Address is required")
        String address,

        @NotBlank(message = "Phone is required")
        String phone,

        @NotBlank(message = "Logo URL is required")
        String logoUrl,

        @NotNull(message = "Owner ID is required")
        Long ownerId
) {
}
