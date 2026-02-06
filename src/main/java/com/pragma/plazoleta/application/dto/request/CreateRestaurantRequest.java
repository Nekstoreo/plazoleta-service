package com.pragma.plazoleta.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateRestaurantRequest(
        @NotBlank(message = "Name is required")
        String name,

        @NotBlank(message = "NIT is required")
        @Pattern(regexp = "^\\d+$", message = "NIT must be numeric only")
        String nit,

        @NotBlank(message = "Address is required")
        String address,

        @NotBlank(message = "Phone is required")
        @Size(max = 13, message = "Phone must have a maximum of 13 characters")
        @Pattern(regexp = "^\\+?\\d{1,12}$", message = "Phone must be numeric and may contain the + symbol at the start")
        String phone,

        @NotBlank(message = "Logo URL is required")
        String logoUrl,

        @NotNull(message = "Owner ID is required")
        Long ownerId
) {
}
