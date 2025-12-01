package com.pragma.plazoleta.application.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DishUpdateRequestDto {

    @NotNull(message = "Price is required")
    @Min(value = 1, message = "Price must be greater than zero")
    private Integer price;

    @NotBlank(message = "Description is required")
    private String description;
}
