package com.pragma.plazoleta.domain.model;

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
public class Dish {

    private Long id;
    private String name;
    private Integer price;
    private String description;
    private String imageUrl;
    private String category;
    @Builder.Default
    private Boolean active = true;
    private Long restaurantId;

    public Dish(String name, Integer price, String description, String imageUrl, String category, Long restaurantId) {
        this.name = name;
        this.price = price;
        this.description = description;
        this.imageUrl = imageUrl;
        this.category = category;
        this.restaurantId = restaurantId;
        this.active = true;
    }
}
