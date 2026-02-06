package com.pragma.plazoleta.infrastructure.output.jpa.adapter;

import com.pragma.plazoleta.domain.model.Dish;
import com.pragma.plazoleta.domain.model.PagedResult;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
    private static final String DISH_NAME = "Hamburguesa Clásica";
    private static final String DISH_CATEGORY = "Hamburguesas";
    private static final String DISH_DESCRIPTION = "Deliciosa hamburguesa con carne 100% res";
    private static final String DISH_IMAGE_URL = "https://example.com/burger.jpg";

    @BeforeEach
    void setUp() {
        dish = new Dish(
                DISH_NAME,
                25000,
                DISH_DESCRIPTION,
                DISH_IMAGE_URL,
                DISH_CATEGORY,
                RESTAURANT_ID
        );
        dish.setActive(true);

        RestaurantEntity restaurantEntity = new RestaurantEntity();
        restaurantEntity.setId(RESTAURANT_ID);

        dishEntity = DishEntity.builder()
                .name(DISH_NAME)
                .price(25000)
                .description(DISH_DESCRIPTION)
                .imageUrl(DISH_IMAGE_URL)
                .category(DISH_CATEGORY)
                .active(true)
                .restaurant(restaurantEntity)
                .build();

        savedDishEntity = DishEntity.builder()
                .id(DISH_ID)
                .name(DISH_NAME)
                .price(25000)
                .description(DISH_DESCRIPTION)
                .imageUrl(DISH_IMAGE_URL)
                .category(DISH_CATEGORY)
                .active(true)
                .restaurant(restaurantEntity)
                .build();

        savedDish = new Dish(
                DISH_NAME,
                25000,
                DISH_DESCRIPTION,
                DISH_IMAGE_URL,
                DISH_CATEGORY,
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
            assertThat(result.getName()).isEqualTo(DISH_NAME);
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
            assertThat(result.get().getName()).isEqualTo(DISH_NAME);

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

    @Nested
    @DisplayName("Find Active Dishes By Restaurant Id Tests")
    class FindActiveDishesByRestaurantIdTests {

        @Test
        @DisplayName("Should return paginated dishes when found")
        void shouldReturnPaginatedDishesWhenFound() {
            RestaurantEntity restaurantEntity = new RestaurantEntity();
            restaurantEntity.setId(RESTAURANT_ID);

            DishEntity dishEntity1 = createDishEntity(1L, "Hamburguesa", "Hamburguesas", restaurantEntity);
            DishEntity dishEntity2 = createDishEntity(2L, "Pizza", "Pizzas", restaurantEntity);
            List<DishEntity> entities = Arrays.asList(dishEntity1, dishEntity2);
            Page<DishEntity> page = new PageImpl<>(entities, PageRequest.of(0, 10), 2);

            Dish dish1 = createDish(1L, "Hamburguesa", "Hamburguesas");
            Dish dish2 = createDish(2L, "Pizza", "Pizzas");

            when(dishRepository.findByRestaurantIdAndActiveTrue(eq(RESTAURANT_ID), any(Pageable.class)))
                    .thenReturn(page);
            when(dishEntityMapper.toDish(dishEntity1)).thenReturn(dish1);
            when(dishEntityMapper.toDish(dishEntity2)).thenReturn(dish2);

            PagedResult<Dish> result = dishJpaAdapter.findActiveDishesByRestaurantId(RESTAURANT_ID, 0, 10);

            assertThat(result).isNotNull();
            assertThat(result.getContent()).hasSize(2);
            assertThat(result.getPage()).isZero();
            assertThat(result.getSize()).isEqualTo(10);
            assertThat(result.getTotalElements()).isEqualTo(2);
            assertThat(result.getTotalPages()).isEqualTo(1);

            verify(dishRepository).findByRestaurantIdAndActiveTrue(eq(RESTAURANT_ID), any(Pageable.class));
        }

        @Test
        @DisplayName("Should return empty result when no dishes found")
        void shouldReturnEmptyResultWhenNoDishesFound() {
            Page<DishEntity> emptyPage = new PageImpl<>(List.of(), PageRequest.of(0, 10), 0);

            when(dishRepository.findByRestaurantIdAndActiveTrue(eq(RESTAURANT_ID), any(Pageable.class)))
                    .thenReturn(emptyPage);

            PagedResult<Dish> result = dishJpaAdapter.findActiveDishesByRestaurantId(RESTAURANT_ID, 0, 10);

            assertThat(result).isNotNull();
            assertThat(result.getContent()).isEmpty();
            assertThat(result.getTotalElements()).isZero();
        }
    }

    @Nested
    @DisplayName("Find Active Dishes By Restaurant Id And Category Tests")
    class FindActiveDishesByRestaurantIdAndCategoryTests {

        @Test
        @DisplayName("Should return paginated dishes filtered by category")
        void shouldReturnPaginatedDishesFilteredByCategory() {
            String category = DISH_CATEGORY;
            RestaurantEntity restaurantEntity = new RestaurantEntity();
            restaurantEntity.setId(RESTAURANT_ID);

            DishEntity dishEntity1 = createDishEntity(1L, DISH_NAME, category, restaurantEntity);
            DishEntity dishEntity2 = createDishEntity(2L, "Hamburguesa BBQ", category, restaurantEntity);
            List<DishEntity> entities = Arrays.asList(dishEntity1, dishEntity2);
            Page<DishEntity> page = new PageImpl<>(entities, PageRequest.of(0, 10), 2);

            Dish dish1 = createDish(1L, DISH_NAME, category);
            Dish dish2 = createDish(2L, "Hamburguesa BBQ", category);

            when(dishRepository.findByRestaurantIdAndCategoryIgnoreCaseAndActiveTrue(
                    eq(RESTAURANT_ID), eq(category), any(Pageable.class)))
                    .thenReturn(page);
            when(dishEntityMapper.toDish(dishEntity1)).thenReturn(dish1);
            when(dishEntityMapper.toDish(dishEntity2)).thenReturn(dish2);

            PagedResult<Dish> result = dishJpaAdapter.findActiveDishesByRestaurantIdAndCategory(
                    RESTAURANT_ID, category, 0, 10);

            assertThat(result).isNotNull();
            assertThat(result.getContent()).hasSize(2);
            assertThat(result.getContent()).allMatch(d -> category.equals(d.getCategory()));

            verify(dishRepository).findByRestaurantIdAndCategoryIgnoreCaseAndActiveTrue(
                    eq(RESTAURANT_ID), eq(category), any(Pageable.class));
        }

        @Test
        @DisplayName("Should return empty result when no dishes match category")
        void shouldReturnEmptyResultWhenNoDishesMatchCategory() {
            String category = "NonExistentCategory";
            Page<DishEntity> emptyPage = new PageImpl<>(List.of(), PageRequest.of(0, 10), 0);

            when(dishRepository.findByRestaurantIdAndCategoryIgnoreCaseAndActiveTrue(
                    eq(RESTAURANT_ID), eq(category), any(Pageable.class)))
                    .thenReturn(emptyPage);

            PagedResult<Dish> result = dishJpaAdapter.findActiveDishesByRestaurantIdAndCategory(
                    RESTAURANT_ID, category, 0, 10);

            assertThat(result).isNotNull();
            assertThat(result.getContent()).isEmpty();
            assertThat(result.getTotalElements()).isZero();
        }
    }

    private DishEntity createDishEntity(Long id, String name, String category, RestaurantEntity restaurant) {
        return DishEntity.builder()
                .id(id)
                .name(name)
                .price(25000)
                .description("Descripción del plato")
                .imageUrl("https://example.com/image.jpg")
                .category(category)
                .active(true)
                .restaurant(restaurant)
                .build();
    }

    private Dish createDish(Long id, String name, String category) {
        Dish createdDish = new Dish(
                name,
                25000,
                "Descripción del plato",
                "https://example.com/image.jpg",
                category,
                RESTAURANT_ID
        );
        createdDish.setId(id);
        createdDish.setActive(true);
        return createdDish;
    }
}
