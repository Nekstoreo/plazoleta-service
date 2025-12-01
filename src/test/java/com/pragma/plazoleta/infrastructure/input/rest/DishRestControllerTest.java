package com.pragma.plazoleta.infrastructure.input.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pragma.plazoleta.application.dto.request.DishRequestDto;
import com.pragma.plazoleta.application.dto.response.DishResponseDto;
import com.pragma.plazoleta.application.handler.IDishHandler;
import com.pragma.plazoleta.domain.exception.InvalidPriceException;
import com.pragma.plazoleta.domain.exception.RestaurantNotFoundException;
import com.pragma.plazoleta.domain.exception.UserNotRestaurantOwnerException;
import com.pragma.plazoleta.infrastructure.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class DishRestControllerTest {

    @Mock
    private IDishHandler dishHandler;

    @InjectMocks
    private DishRestController dishRestController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private DishRequestDto validRequest;
    private DishResponseDto responseDto;
    private static final Long OWNER_ID = 1L;
    private static final Long RESTAURANT_ID = 10L;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(dishRestController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();

        validRequest = DishRequestDto.builder()
                .name("Hamburguesa Clásica")
                .price(25000)
                .description("Deliciosa hamburguesa con carne 100% res")
                .imageUrl("https://example.com/burger.jpg")
                .category("Hamburguesas")
                .restaurantId(RESTAURANT_ID)
                .build();

        responseDto = DishResponseDto.builder()
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

    @Nested
    @DisplayName("Create Dish - Happy Path")
    class CreateDishHappyPath {

        @Test
        @DisplayName("Should create dish and return 201")
        void shouldCreateDishAndReturn201() throws Exception {
            when(dishHandler.createDish(any(DishRequestDto.class), eq(OWNER_ID)))
                    .thenReturn(responseDto);

            mockMvc.perform(post("/api/v1/dishes")
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("X-Owner-Id", OWNER_ID)
                            .content(objectMapper.writeValueAsString(validRequest)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.name").value("Hamburguesa Clásica"))
                    .andExpect(jsonPath("$.price").value(25000))
                    .andExpect(jsonPath("$.active").value(true))
                    .andExpect(jsonPath("$.restaurantId").value(RESTAURANT_ID));
        }
    }

    @Nested
    @DisplayName("Create Dish - Validation Errors")
    class CreateDishValidationErrors {

        @Test
        @DisplayName("Should return 400 when name is blank")
        void shouldReturn400WhenNameIsBlank() throws Exception {
            validRequest.setName("");

            mockMvc.perform(post("/api/v1/dishes")
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("X-Owner-Id", OWNER_ID)
                            .content(objectMapper.writeValueAsString(validRequest)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 400 when price is less than 1")
        void shouldReturn400WhenPriceIsLessThan1() throws Exception {
            validRequest.setPrice(0);

            mockMvc.perform(post("/api/v1/dishes")
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("X-Owner-Id", OWNER_ID)
                            .content(objectMapper.writeValueAsString(validRequest)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 400 when restaurantId is null")
        void shouldReturn400WhenRestaurantIdIsNull() throws Exception {
            validRequest.setRestaurantId(null);

            mockMvc.perform(post("/api/v1/dishes")
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("X-Owner-Id", OWNER_ID)
                            .content(objectMapper.writeValueAsString(validRequest)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 400 when X-Owner-Id header is missing")
        void shouldReturn400WhenOwnerIdHeaderIsMissing() throws Exception {
            mockMvc.perform(post("/api/v1/dishes")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validRequest)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("Create Dish - Business Errors")
    class CreateDishBusinessErrors {

        @Test
        @DisplayName("Should return 400 when price is invalid")
        void shouldReturn400WhenPriceIsInvalid() throws Exception {
            when(dishHandler.createDish(any(DishRequestDto.class), eq(OWNER_ID)))
                    .thenThrow(new InvalidPriceException());

            mockMvc.perform(post("/api/v1/dishes")
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("X-Owner-Id", OWNER_ID)
                            .content(objectMapper.writeValueAsString(validRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Price must be a positive integer greater than zero"));
        }

        @Test
        @DisplayName("Should return 404 when restaurant not found")
        void shouldReturn404WhenRestaurantNotFound() throws Exception {
            when(dishHandler.createDish(any(DishRequestDto.class), eq(OWNER_ID)))
                    .thenThrow(new RestaurantNotFoundException(RESTAURANT_ID));

            mockMvc.perform(post("/api/v1/dishes")
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("X-Owner-Id", OWNER_ID)
                            .content(objectMapper.writeValueAsString(validRequest)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("Restaurant not found with id: " + RESTAURANT_ID));
        }

        @Test
        @DisplayName("Should return 403 when user is not restaurant owner")
        void shouldReturn403WhenUserIsNotOwner() throws Exception {
            Long wrongOwnerId = 999L;
            when(dishHandler.createDish(any(DishRequestDto.class), eq(wrongOwnerId)))
                    .thenThrow(new UserNotRestaurantOwnerException(wrongOwnerId, RESTAURANT_ID));

            mockMvc.perform(post("/api/v1/dishes")
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("X-Owner-Id", wrongOwnerId)
                            .content(objectMapper.writeValueAsString(validRequest)))
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.message").value("User with id " + wrongOwnerId + " is not the owner of restaurant with id " + RESTAURANT_ID));
        }
    }
}
