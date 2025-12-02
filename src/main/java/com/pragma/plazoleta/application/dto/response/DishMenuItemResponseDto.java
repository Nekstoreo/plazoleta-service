package com.pragma.plazoleta.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DishMenuItemResponseDto {

    private Long id;
    private String name;
    private Integer price;
    private String description;
    private String imageUrl;
    private String category;
}
