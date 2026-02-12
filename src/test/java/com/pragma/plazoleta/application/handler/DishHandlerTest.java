package com.pragma.plazoleta.application.handler;

import com.pragma.plazoleta.application.dto.request.DishActiveRequestDto;
import com.pragma.plazoleta.application.dto.request.DishRequestDto;
import com.pragma.plazoleta.application.dto.request.DishUpdateRequestDto;
import com.pragma.plazoleta.application.dto.response.DishMenuItemResponseDto;
import com.pragma.plazoleta.application.dto.response.DishResponseDto;
import com.pragma.plazoleta.application.dto.response.PagedResponse;
import com.pragma.plazoleta.application.mapper.DishDtoMapper;
import com.pragma.plazoleta.domain.api.IDishServicePort;
import com.pragma.plazoleta.domain.model.Dish;
import com.pragma.plazoleta.domain.model.PagedResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DishHandlerTest {

    @Mock
    private IDishServicePort dishServicePort;

    @Mock
    private DishDtoMapper dishDtoMapper;

    @InjectMocks
    private DishHandler dishHandler;

    private DishRequestDto dishRequestDto;
    private Dish dish;
    private Dish savedDish;
    private DishResponseDto dishResponseDto;
    private static final Long DISH_ID = 1L;
    private static final Long OWNER_ID = 1L;
    private static final Long RESTAURANT_ID = 10L;
    private static final String DISH_NAME = "Hamburguesa Clásica";
    private static final String DISH_CATEGORY = "Hamburguesas";
    private static final String DISH_DESCRIPTION = "Deliciosa hamburguesa con carne 100% res";
    private static final String DISH_IMAGE_URL = "https://example.com/burger.jpg";

    @BeforeEach
    void setUp() {
        dishRequestDto = DishRequestDto.builder()
                .name(DISH_NAME)
                .price(25000)
                .description(DISH_DESCRIPTION)
                .imageUrl(DISH_IMAGE_URL)
                .category(DISH_CATEGORY)
                .restaurantId(RESTAURANT_ID)
                .build();

        dish = new Dish(
                DISH_NAME,
                25000,
                DISH_DESCRIPTION,
                DISH_IMAGE_URL,
                DISH_CATEGORY,
                RESTAURANT_ID
        );

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

        dishResponseDto = DishResponseDto.builder()
                .id(DISH_ID)
                .name(DISH_NAME)
                .price(25000)
                .description(DISH_DESCRIPTION)
                .imageUrl(DISH_IMAGE_URL)
                .category(DISH_CATEGORY)
                .active(true)
                .restaurantId(RESTAURANT_ID)
                .build();
    }

    @Nested
    @DisplayName("Create Dish Tests")
    class CreateDishTests {

        @Test
        @DisplayName("Should create dish and return response dto")
        void shouldCreateDishAndReturnResponseDto() {
            when(dishDtoMapper.toDish(dishRequestDto)).thenReturn(dish);
            when(dishServicePort.createDish(any(Dish.class), eq(OWNER_ID))).thenReturn(savedDish);
            when(dishDtoMapper.toDishResponseDto(savedDish)).thenReturn(dishResponseDto);

            DishResponseDto result = dishHandler.createDish(dishRequestDto, OWNER_ID);

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(DISH_ID);
            assertThat(result.getName()).isEqualTo(DISH_NAME);
            assertThat(result.getPrice()).isEqualTo(25000);
            assertThat(result.getActive()).isTrue();

            verify(dishDtoMapper).toDish(dishRequestDto);
            verify(dishServicePort).createDish(any(Dish.class), eq(OWNER_ID));
            verify(dishDtoMapper).toDishResponseDto(savedDish);
        }
    }

    @Nested
    @DisplayName("Update Dish Tests")
    class UpdateDishTests {

        @Test
        @DisplayName("Should update dish and return response dto")
        void shouldUpdateDishAndReturnResponseDto() {
            Integer newPrice = 30000;
            String newDescription = "Nueva descripción actualizada";

            DishUpdateRequestDto updateRequestDto = DishUpdateRequestDto.builder()
                    .price(newPrice)
                    .description(newDescription)
                    .build();

            Dish updatedDish = new Dish(
                    DISH_NAME,
                    newPrice,
                    newDescription,
                    DISH_IMAGE_URL,
                    DISH_CATEGORY,
                    RESTAURANT_ID
            );
            updatedDish.setId(DISH_ID);
            updatedDish.setActive(true);

            DishResponseDto updatedResponseDto = DishResponseDto.builder()
                    .id(DISH_ID)
                    .name(DISH_NAME)
                    .price(newPrice)
                    .description(newDescription)
                    .imageUrl(DISH_IMAGE_URL)
                    .category(DISH_CATEGORY)
                    .active(true)
                    .restaurantId(RESTAURANT_ID)
                    .build();

            when(dishServicePort.updateDish(DISH_ID, newPrice, newDescription, OWNER_ID))
                    .thenReturn(updatedDish);
            when(dishDtoMapper.toDishResponseDto(updatedDish)).thenReturn(updatedResponseDto);

            DishResponseDto result = dishHandler.updateDish(DISH_ID, updateRequestDto, OWNER_ID);

            assertThat(result).isNotNull();
            assertThat(result.getPrice()).isEqualTo(newPrice);
            assertThat(result.getDescription()).isEqualTo(newDescription);

            verify(dishServicePort).updateDish(DISH_ID, newPrice, newDescription, OWNER_ID);
            verify(dishDtoMapper).toDishResponseDto(updatedDish);
        }
    }

    @Nested
    @DisplayName("Change Dish Active Status Tests")
    class ChangeDishActiveStatusTests {

        @Test
        @DisplayName("Should change dish active flag and return response dto")
        void shouldChangeDishActiveFlagAndReturnResponseDto() {
            DishActiveRequestDto activeRequest = DishActiveRequestDto.builder()
                    .active(false)
                    .build();

            Dish deactivatedDish = new Dish(
                    DISH_NAME,
                    25000,
                    DISH_DESCRIPTION,
                    DISH_IMAGE_URL,
                    DISH_CATEGORY,
                    RESTAURANT_ID
            );
            deactivatedDish.setId(DISH_ID);
            deactivatedDish.setActive(false);

            DishResponseDto deactivatedResponse = DishResponseDto.builder()
                    .id(DISH_ID)
                    .name(DISH_NAME)
                    .price(25000)
                    .description(DISH_DESCRIPTION)
                    .imageUrl(DISH_IMAGE_URL)
                    .category(DISH_CATEGORY)
                    .active(false)
                    .restaurantId(RESTAURANT_ID)
                    .build();

            when(dishServicePort.changeDishActiveStatus(DISH_ID, false, OWNER_ID))
                    .thenReturn(deactivatedDish);
            when(dishDtoMapper.toDishResponseDto(deactivatedDish)).thenReturn(deactivatedResponse);

            DishResponseDto result = dishHandler.changeDishActiveStatus(DISH_ID, activeRequest, OWNER_ID);

            assertThat(result).isNotNull();
            assertThat(result.getActive()).isFalse();

            verify(dishServicePort).changeDishActiveStatus(DISH_ID, false, OWNER_ID);
            verify(dishDtoMapper).toDishResponseDto(deactivatedDish);
        }
    }

    @Nested
    @DisplayName("Get Dishes By Restaurant Tests")
    class GetDishesByRestaurantTests {

        @Test
        @DisplayName("Should return paginated dishes without category filter")
        void shouldReturnPaginatedDishesWithoutCategoryFilter() {
            Dish dish1 = createDish(1L, "Hamburguesa", DISH_CATEGORY);
            Dish dish2 = createDish(2L, "Pizza", "Pizzas");
            List<Dish> dishes = Arrays.asList(dish1, dish2);
            PagedResult<Dish> pagedResult = PagedResult.of(dishes, 0, 10, 2, 1);

            DishMenuItemResponseDto menuItem1 = createMenuItemDto(1L, "Hamburguesa", DISH_CATEGORY);
            DishMenuItemResponseDto menuItem2 = createMenuItemDto(2L, "Pizza", "Pizzas");

            when(dishServicePort.getDishesByRestaurant(RESTAURANT_ID, null, 0, 10))
                    .thenReturn(pagedResult);
            when(dishDtoMapper.toDishMenuItemResponseDto(dish1)).thenReturn(menuItem1);
            when(dishDtoMapper.toDishMenuItemResponseDto(dish2)).thenReturn(menuItem2);

            PagedResponse<DishMenuItemResponseDto> result = dishHandler.getDishesByRestaurant(
                    RESTAURANT_ID, null, 0, 10);

            assertThat(result).isNotNull();
            assertThat(result.getContent()).hasSize(2);
            assertThat(result.getPage()).isZero();
            assertThat(result.getSize()).isEqualTo(10);
            assertThat(result.getTotalElements()).isEqualTo(2);
            assertThat(result.getTotalPages()).isEqualTo(1);
            assertThat(result.isFirst()).isTrue();
            assertThat(result.isLast()).isTrue();

            verify(dishServicePort).getDishesByRestaurant(RESTAURANT_ID, null, 0, 10);
        }

        @Test
        @DisplayName("Should return paginated dishes with category filter")
        void shouldReturnPaginatedDishesWithCategoryFilter() {
            String category = DISH_CATEGORY;
            Dish dish1 = createDish(1L, DISH_NAME, category);
            List<Dish> dishes = List.of(dish1);
            PagedResult<Dish> pagedResult = PagedResult.of(dishes, 0, 10, 1, 1);

            DishMenuItemResponseDto menuItem1 = createMenuItemDto(1L, DISH_NAME, category);

            when(dishServicePort.getDishesByRestaurant(RESTAURANT_ID, category, 0, 10))
                    .thenReturn(pagedResult);
            when(dishDtoMapper.toDishMenuItemResponseDto(dish1)).thenReturn(menuItem1);

            PagedResponse<DishMenuItemResponseDto> result = dishHandler.getDishesByRestaurant(
                    RESTAURANT_ID, category, 0, 10);

            assertThat(result).isNotNull();
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).getCategory()).isEqualTo(category);

            verify(dishServicePort).getDishesByRestaurant(RESTAURANT_ID, category, 0, 10);
        }

        @Test
        @DisplayName("Should return empty result when no dishes found")
        void shouldReturnEmptyResultWhenNoDishesFound() {
            PagedResult<Dish> emptyResult = PagedResult.of(List.of(), 0, 10, 0, 0);

            when(dishServicePort.getDishesByRestaurant(RESTAURANT_ID, null, 0, 10))
                    .thenReturn(emptyResult);

            PagedResponse<DishMenuItemResponseDto> result = dishHandler.getDishesByRestaurant(
                    RESTAURANT_ID, null, 0, 10);

            assertThat(result).isNotNull();
            assertThat(result.getContent()).isEmpty();
            assertThat(result.getTotalElements()).isZero();

            verify(dishServicePort).getDishesByRestaurant(RESTAURANT_ID, null, 0, 10);
        }

        private Dish createDish(Long id, String name, String category) {
            Dish createdDish = new Dish(
                    name,
                    25000,
                    "Dish description", 
                    "https://example.com/image.jpg",
                    category,
                    RESTAURANT_ID
            );
            createdDish.setId(id);
            createdDish.setActive(true);
            return createdDish;
        }

        private DishMenuItemResponseDto createMenuItemDto(Long id, String name, String category) {
            return DishMenuItemResponseDto.builder()
                    .id(id)
                    .name(name)
                    .price(25000)
                    .description("Dish description")
                    .imageUrl("https://example.com/image.jpg")
                    .category(category)
                    .build();
        }
    }
}
