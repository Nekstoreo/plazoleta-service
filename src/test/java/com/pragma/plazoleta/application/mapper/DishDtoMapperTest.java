package com.pragma.plazoleta.application.mapper;

import com.pragma.plazoleta.application.dto.request.DishRequestDto;
import com.pragma.plazoleta.application.dto.response.DishMenuItemResponseDto;
import com.pragma.plazoleta.application.dto.response.DishResponseDto;
import com.pragma.plazoleta.domain.model.Dish;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;

class DishDtoMapperTest {
    private static final String HAMBURGER = "Hamburger";
    private static final String FAST_FOOD = "FastFood";
    private static final String PIZZA = "Pizza";
    private static final String ITALIAN = "Italian";
    private static final String SUSHI = "Sushi";
    private static final String JAPANESE = "Japanese";

    private final DishDtoMapper mapper = Mappers.getMapper(DishDtoMapper.class);

    @Test
    void toDish_shouldMapFields() {
        DishRequestDto dto = DishRequestDto.builder()
                .name(HAMBURGER)
                .price(12000)
                .description("Tasty")
                .imageUrl("http://img")
                .category(FAST_FOOD)
                .restaurantId(1L)
                .build();

        Dish result = mapper.toDish(dto);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(dto.getName());
        assertThat(result.getPrice()).isEqualTo(dto.getPrice());
        assertThat(result.getDescription()).isEqualTo(dto.getDescription());
        assertThat(result.getImageUrl()).isEqualTo(dto.getImageUrl());
        assertThat(result.getCategory()).isEqualTo(dto.getCategory());
        assertThat(result.getRestaurantId()).isEqualTo(dto.getRestaurantId());
    }

    @Test
    void toDishResponseDto_shouldMapFields() {
        Dish dish = new Dish();
        dish.setId(5L);
        dish.setName(PIZZA);
        dish.setPrice(25000);
        dish.setDescription("Delicious");
        dish.setImageUrl("http://p");
        dish.setCategory(ITALIAN);
        dish.setActive(true);
        dish.setRestaurantId(2L);

        DishResponseDto dto = mapper.toDishResponseDto(dish);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(dish.getId());
        assertThat(dto.getName()).isEqualTo(dish.getName());
        assertThat(dto.getPrice()).isEqualTo(dish.getPrice());
        assertThat(dto.getDescription()).isEqualTo(dish.getDescription());
        assertThat(dto.getImageUrl()).isEqualTo(dish.getImageUrl());
        assertThat(dto.getCategory()).isEqualTo(dish.getCategory());
        assertThat(dto.getActive()).isEqualTo(dish.getActive());
        assertThat(dto.getRestaurantId()).isEqualTo(dish.getRestaurantId());
    }

    @Test
    void toDishMenuItemResponseDto_shouldMapFields() {
        Dish dish = new Dish();
        dish.setId(10L);
        dish.setName(SUSHI);
        dish.setPrice(35000);
        dish.setDescription("Fresh");
        dish.setImageUrl("http://s");
        dish.setCategory(JAPANESE);

        DishMenuItemResponseDto dto = mapper.toDishMenuItemResponseDto(dish);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(dish.getId());
        assertThat(dto.getName()).isEqualTo(dish.getName());
        assertThat(dto.getPrice()).isEqualTo(dish.getPrice());
        assertThat(dto.getDescription()).isEqualTo(dish.getDescription());
        assertThat(dto.getImageUrl()).isEqualTo(dish.getImageUrl());
        assertThat(dto.getCategory()).isEqualTo(dish.getCategory());
    }

    @Test
    void toDish_shouldReturnNull_whenDtoIsNull() {
        assertThat(mapper.toDish(null)).isNull();
    }

    @Test
    void toDishResponseDto_shouldReturnNull_whenDishIsNull() {
        assertThat(mapper.toDishResponseDto(null)).isNull();
    }

    @Test
    void toDishMenuItemResponseDto_shouldReturnNull_whenDishIsNull() {
        assertThat(mapper.toDishMenuItemResponseDto(null)).isNull();
    }
}
