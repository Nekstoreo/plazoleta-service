package com.pragma.plazoleta.infrastructure.input.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pragma.plazoleta.application.dto.request.DishRequestDto;
import com.pragma.plazoleta.application.dto.request.DishUpdateRequestDto;
import com.pragma.plazoleta.application.dto.response.DishResponseDto;
import com.pragma.plazoleta.application.handler.IDishHandler;
import com.pragma.plazoleta.domain.exception.DishNotFoundException;
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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
    private static final Long DISH_ID = 1L;
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
                .id(DISH_ID)
                .name("Hamburguesa Clásica")
                .price(25000)
                .description("Deliciosa hamburguesa con carne 100% res")
                .imageUrl("https://example.com/burger.jpg")
                .category("Hamburguesas")
                .active(true)
                .restaurantId(RESTAURANT_ID)
                .build();

        // Setup security context with authenticated user
        setUpSecurityContext(OWNER_ID, "OWNER");
    }

    private void setUpSecurityContext(Long userId, String role) {
        List<SimpleGrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority("ROLE_" + role)
        );
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken("owner@test.com", null, authorities);
        authentication.setDetails(userId);
        SecurityContextHolder.getContext().setAuthentication(authentication);
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
                            .content(objectMapper.writeValueAsString(validRequest)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(DISH_ID))
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
                            .content(objectMapper.writeValueAsString(validRequest)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 400 when price is less than 1")
        void shouldReturn400WhenPriceIsLessThan1() throws Exception {
            validRequest.setPrice(0);

            mockMvc.perform(post("/api/v1/dishes")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validRequest)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 400 when restaurantId is null")
        void shouldReturn400WhenRestaurantIdIsNull() throws Exception {
            validRequest.setRestaurantId(null);

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
                            .content(objectMapper.writeValueAsString(validRequest)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("Restaurant not found with id: " + RESTAURANT_ID));
        }

        @Test
        @DisplayName("Should return 403 when user is not restaurant owner")
        void shouldReturn403WhenUserIsNotOwner() throws Exception {
            Long wrongOwnerId = 999L;
            setUpSecurityContext(wrongOwnerId, "OWNER");

            when(dishHandler.createDish(any(DishRequestDto.class), eq(wrongOwnerId)))
                    .thenThrow(new UserNotRestaurantOwnerException(wrongOwnerId, RESTAURANT_ID));

            mockMvc.perform(post("/api/v1/dishes")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validRequest)))
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.message").value("User with id " + wrongOwnerId + " is not the owner of restaurant with id " + RESTAURANT_ID));
        }
    }

    @Nested
    @DisplayName("Update Dish - Happy Path")
    class UpdateDishHappyPath {

        @Test
        @DisplayName("Should update dish and return 200")
        void shouldUpdateDishAndReturn200() throws Exception {
            Integer newPrice = 30000;
            String newDescription = "Nueva descripción actualizada";

            DishUpdateRequestDto updateRequest = DishUpdateRequestDto.builder()
                    .price(newPrice)
                    .description(newDescription)
                    .build();

            DishResponseDto updatedResponse = DishResponseDto.builder()
                    .id(DISH_ID)
                    .name("Hamburguesa Clásica")
                    .price(newPrice)
                    .description(newDescription)
                    .imageUrl("https://example.com/burger.jpg")
                    .category("Hamburguesas")
                    .active(true)
                    .restaurantId(RESTAURANT_ID)
                    .build();

            when(dishHandler.updateDish(eq(DISH_ID), any(DishUpdateRequestDto.class), eq(OWNER_ID)))
                    .thenReturn(updatedResponse);

            mockMvc.perform(patch("/api/v1/dishes/{dishId}", DISH_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(DISH_ID))
                    .andExpect(jsonPath("$.price").value(newPrice))
                    .andExpect(jsonPath("$.description").value(newDescription));
        }
    }

    @Nested
    @DisplayName("Update Dish - Validation Errors")
    class UpdateDishValidationErrors {

        @Test
        @DisplayName("Should return 400 when price is less than 1")
        void shouldReturn400WhenPriceIsLessThan1() throws Exception {
            DishUpdateRequestDto updateRequest = DishUpdateRequestDto.builder()
                    .price(0)
                    .description("Nueva descripción")
                    .build();

            mockMvc.perform(patch("/api/v1/dishes/{dishId}", DISH_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateRequest)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 400 when description is blank")
        void shouldReturn400WhenDescriptionIsBlank() throws Exception {
            DishUpdateRequestDto updateRequest = DishUpdateRequestDto.builder()
                    .price(30000)
                    .description("")
                    .build();

            mockMvc.perform(patch("/api/v1/dishes/{dishId}", DISH_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateRequest)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("Update Dish - Business Errors")
    class UpdateDishBusinessErrors {

        @Test
        @DisplayName("Should return 404 when dish not found")
        void shouldReturn404WhenDishNotFound() throws Exception {
            DishUpdateRequestDto updateRequest = DishUpdateRequestDto.builder()
                    .price(30000)
                    .description("Nueva descripción")
                    .build();

            when(dishHandler.updateDish(eq(DISH_ID), any(DishUpdateRequestDto.class), eq(OWNER_ID)))
                    .thenThrow(new DishNotFoundException(DISH_ID));

            mockMvc.perform(patch("/api/v1/dishes/{dishId}", DISH_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateRequest)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("Dish not found with id: " + DISH_ID));
        }

        @Test
        @DisplayName("Should return 403 when user is not restaurant owner")
        void shouldReturn403WhenUserIsNotOwner() throws Exception {
            Long wrongOwnerId = 999L;
            setUpSecurityContext(wrongOwnerId, "OWNER");

            DishUpdateRequestDto updateRequest = DishUpdateRequestDto.builder()
                    .price(30000)
                    .description("Nueva descripción")
                    .build();

            when(dishHandler.updateDish(eq(DISH_ID), any(DishUpdateRequestDto.class), eq(wrongOwnerId)))
                    .thenThrow(new UserNotRestaurantOwnerException(wrongOwnerId, RESTAURANT_ID));

            mockMvc.perform(patch("/api/v1/dishes/{dishId}", DISH_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateRequest)))
                    .andExpect(status().isForbidden());
        }
    }
}
