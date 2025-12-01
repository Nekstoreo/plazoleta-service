package com.pragma.plazoleta.application.handler;

import com.pragma.plazoleta.application.dto.request.DishRequestDto;
import com.pragma.plazoleta.application.dto.response.DishResponseDto;
import com.pragma.plazoleta.application.mapper.DishDtoMapper;
import com.pragma.plazoleta.domain.api.IDishServicePort;
import com.pragma.plazoleta.domain.model.Dish;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
    private static final Long OWNER_ID = 1L;
    private static final Long RESTAURANT_ID = 10L;

    @BeforeEach
    void setUp() {
        dishRequestDto = DishRequestDto.builder()
                .name("Hamburguesa Clásica")
                .price(25000)
                .description("Deliciosa hamburguesa con carne 100% res")
                .imageUrl("https://example.com/burger.jpg")
                .category("Hamburguesas")
                .restaurantId(RESTAURANT_ID)
                .build();

        dish = new Dish(
                "Hamburguesa Clásica",
                25000,
                "Deliciosa hamburguesa con carne 100% res",
                "https://example.com/burger.jpg",
                "Hamburguesas",
                RESTAURANT_ID
        );

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

        dishResponseDto = DishResponseDto.builder()
                .id(1L)
                .name("Hamburguesa Clásica")
                .price(25000)
                .description("Deliciosa hamburguesa con carne 100% res")
                .imageUrl("https://example.com/burger.jpg")
                .category("Hamburguesas")
                .active(true)
                .restaurantId(RESTAURANT_ID)
                .build();
    }

    @Test
    @DisplayName("Should create dish and return response dto")
    void shouldCreateDishAndReturnResponseDto() {
        when(dishDtoMapper.toDish(dishRequestDto)).thenReturn(dish);
        when(dishServicePort.createDish(any(Dish.class), eq(OWNER_ID))).thenReturn(savedDish);
        when(dishDtoMapper.toDishResponseDto(savedDish)).thenReturn(dishResponseDto);

        DishResponseDto result = dishHandler.createDish(dishRequestDto, OWNER_ID);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Hamburguesa Clásica");
        assertThat(result.getPrice()).isEqualTo(25000);
        assertThat(result.getActive()).isTrue();

        verify(dishDtoMapper).toDish(dishRequestDto);
        verify(dishServicePort).createDish(any(Dish.class), eq(OWNER_ID));
        verify(dishDtoMapper).toDishResponseDto(savedDish);
    }
}
