package com.pragma.plazoleta.infrastructure.output.jpa.mapper;

import com.pragma.plazoleta.domain.model.Dish;
import com.pragma.plazoleta.infrastructure.output.jpa.entity.DishEntity;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;

class DishEntityMapperTest {

    private final DishEntityMapper mapper = Mappers.getMapper(DishEntityMapper.class);

    @Test
    void toEntity_shouldMapRestaurantId() {
        Dish d = new Dish();
        d.setId(1L);
        d.setName("X");
        d.setPrice(100);
        d.setDescription("desc");
        d.setImageUrl("img");
        d.setCategory("cat");
        d.setActive(true);
        d.setRestaurantId(42L);

        DishEntity e = mapper.toEntity(d);

        assertThat(e).isNotNull();
        assertThat(e.getRestaurant()).isNotNull();
        assertThat(e.getRestaurant().getId()).isEqualTo(42L);
        assertThat(e.getName()).isEqualTo("X");
    }

    @Test
    void toDish_shouldMapFields() {
        DishEntity e = DishEntity.builder()
                .id(11L)
                .name("Q")
                .price(300)
                .description("dd")
                .imageUrl("u")
                .category("cat2")
                .active(true)
                .build();

        var d = mapper.toDish(e);

        assertThat(d).isNotNull();
        assertThat(d.getId()).isEqualTo(e.getId());
        assertThat(d.getName()).isEqualTo(e.getName());
        assertThat(d.getPrice()).isEqualTo(e.getPrice());
    }
}
