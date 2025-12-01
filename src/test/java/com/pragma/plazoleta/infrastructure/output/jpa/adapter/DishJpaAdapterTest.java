package com.pragma.plazoleta.infrastructure.output.jpa.adapter;

import com.pragma.plazoleta.domain.model.Dish;
import com.pragma.plazoleta.infrastructure.output.jpa.entity.DishEntity;
import com.pragma.plazoleta.infrastructure.output.jpa.entity.RestaurantEntity;
import com.pragma.plazoleta.infrastructure.output.jpa.mapper.DishEntityMapper;
import com.pragma.plazoleta.infrastructure.output.jpa.repository.IDishRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DishJpaAdapterTest {

    @Mock
    private IDishRepository dishRepository;

    @Mock
    private DishEntityMapper dishEntityMapper;

    @InjectMocks
    private DishJpaAdapter dishJpaAdapter;

    private Dish dish;
    private DishEntity dishEntity;
    private DishEntity savedDishEntity;
    private Dish savedDish;
    private static final Long RESTAURANT_ID = 10L;

    @BeforeEach
    void setUp() {
        dish = new Dish(
                "Hamburguesa Clásica",
                25000,
                "Deliciosa hamburguesa con carne 100% res",
                "https://example.com/burger.jpg",
                "Hamburguesas",
                RESTAURANT_ID
        );
        dish.setActive(true);

        RestaurantEntity restaurantEntity = new RestaurantEntity();
        restaurantEntity.setId(RESTAURANT_ID);

        dishEntity = DishEntity.builder()
                .name("Hamburguesa Clásica")
                .price(25000)
                .description("Deliciosa hamburguesa con carne 100% res")
                .imageUrl("https://example.com/burger.jpg")
                .category("Hamburguesas")
                .active(true)
                .restaurant(restaurantEntity)
                .build();

        savedDishEntity = DishEntity.builder()
                .id(1L)
                .name("Hamburguesa Clásica")
                .price(25000)
                .description("Deliciosa hamburguesa con carne 100% res")
                .imageUrl("https://example.com/burger.jpg")
                .category("Hamburguesas")
                .active(true)
                .restaurant(restaurantEntity)
                .build();

        savedDish = new Dish(
                "Hamburguesa Clásica",
                25000,
                "Deliciosa hamburguesa con carne 100% res",
                "https://example.com/burger.jpg",
                "Hamburguesas",
                RESTAURANT_ID
        );
        savedDish.setId(1L);
        savedDish.setActive(true);
    }

    @Test
    @DisplayName("Should save dish and return saved dish")
    void shouldSaveDishAndReturnSavedDish() {
        when(dishEntityMapper.toEntity(dish)).thenReturn(dishEntity);
        when(dishRepository.save(dishEntity)).thenReturn(savedDishEntity);
        when(dishEntityMapper.toDish(savedDishEntity)).thenReturn(savedDish);

        Dish result = dishJpaAdapter.saveDish(dish);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Hamburguesa Clásica");
        assertThat(result.getPrice()).isEqualTo(25000);
        assertThat(result.getActive()).isTrue();

        verify(dishEntityMapper).toEntity(dish);
        verify(dishRepository).save(dishEntity);
        verify(dishEntityMapper).toDish(savedDishEntity);
    }
}
