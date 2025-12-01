package com.pragma.plazoleta.infrastructure.output.jpa.adapter;

import com.pragma.plazoleta.domain.model.Dish;
import com.pragma.plazoleta.infrastructure.output.jpa.entity.DishEntity;
import com.pragma.plazoleta.infrastructure.output.jpa.entity.RestaurantEntity;
import com.pragma.plazoleta.infrastructure.output.jpa.mapper.DishEntityMapper;
import com.pragma.plazoleta.infrastructure.output.jpa.repository.IDishRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

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
    private static final Long DISH_ID = 1L;
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
                .id(DISH_ID)
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
        savedDish.setId(DISH_ID);
        savedDish.setActive(true);
    }

    @Nested
    @DisplayName("Save Dish Tests")
    class SaveDishTests {

        @Test
        @DisplayName("Should save dish and return saved dish")
        void shouldSaveDishAndReturnSavedDish() {
            when(dishEntityMapper.toEntity(dish)).thenReturn(dishEntity);
            when(dishRepository.save(dishEntity)).thenReturn(savedDishEntity);
            when(dishEntityMapper.toDish(savedDishEntity)).thenReturn(savedDish);

            Dish result = dishJpaAdapter.saveDish(dish);

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(DISH_ID);
            assertThat(result.getName()).isEqualTo("Hamburguesa Clásica");
            assertThat(result.getPrice()).isEqualTo(25000);
            assertThat(result.getActive()).isTrue();

            verify(dishEntityMapper).toEntity(dish);
            verify(dishRepository).save(dishEntity);
            verify(dishEntityMapper).toDish(savedDishEntity);
        }
    }

    @Nested
    @DisplayName("Find By Id Tests")
    class FindByIdTests {

        @Test
        @DisplayName("Should return dish when found by id")
        void shouldReturnDishWhenFoundById() {
            when(dishRepository.findById(DISH_ID)).thenReturn(Optional.of(savedDishEntity));
            when(dishEntityMapper.toDish(savedDishEntity)).thenReturn(savedDish);

            Optional<Dish> result = dishJpaAdapter.findById(DISH_ID);

            assertThat(result).isPresent();
            assertThat(result.get().getId()).isEqualTo(DISH_ID);
            assertThat(result.get().getName()).isEqualTo("Hamburguesa Clásica");

            verify(dishRepository).findById(DISH_ID);
        }

        @Test
        @DisplayName("Should return empty optional when dish not found")
        void shouldReturnEmptyOptionalWhenDishNotFound() {
            Long nonExistentId = 999L;
            when(dishRepository.findById(nonExistentId)).thenReturn(Optional.empty());

            Optional<Dish> result = dishJpaAdapter.findById(nonExistentId);

            assertThat(result).isEmpty();
            verify(dishRepository).findById(nonExistentId);
        }
    }
}
