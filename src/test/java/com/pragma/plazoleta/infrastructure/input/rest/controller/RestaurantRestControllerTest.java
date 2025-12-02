package com.pragma.plazoleta.infrastructure.input.rest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pragma.plazoleta.application.dto.request.CreateRestaurantRequest;
import com.pragma.plazoleta.application.dto.response.DishMenuItemResponseDto;
import com.pragma.plazoleta.application.dto.response.PagedResponse;
import com.pragma.plazoleta.application.dto.response.RestaurantResponse;
import com.pragma.plazoleta.application.handler.IDishHandler;
import com.pragma.plazoleta.application.handler.IRestaurantHandler;
import com.pragma.plazoleta.domain.exception.InvalidRestaurantNameException;
import com.pragma.plazoleta.domain.exception.OwnerNotFoundException;
import com.pragma.plazoleta.domain.exception.RestaurantAlreadyExistsException;
import com.pragma.plazoleta.domain.exception.RestaurantNotFoundException;
import com.pragma.plazoleta.domain.exception.UserNotOwnerException;
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

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class RestaurantRestControllerTest {

    private static final String BASE_URL = "/api/v1/restaurants";
    private static final Long OWNER_ID = 1L;
    private static final String RESTAURANT_NAME = "El Buen Sabor";
    private static final String RESTAURANT_NIT = "123456789";
    private static final String RESTAURANT_ADDRESS = "Calle 123 #45-67";
    private static final String RESTAURANT_PHONE = "+573001234567";
    private static final String RESTAURANT_LOGO_URL = "https://example.com/logo.png";
    private static final String MESSAGE_JSON_PATH = "$.message";

    @Mock
    private IRestaurantHandler restaurantHandler;

    @Mock
    private IDishHandler dishHandler;

    @InjectMocks
    private RestaurantRestController restaurantRestController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(restaurantRestController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
    }

    @Nested
    @DisplayName("Create Restaurant - Success Cases")
    class CreateRestaurantSuccessCases {

        @Test
        @DisplayName("Should create restaurant successfully and return 201")
        void shouldCreateRestaurantSuccessfullyAndReturn201() throws Exception {
            // Arrange
            CreateRestaurantRequest request = new CreateRestaurantRequest(
                    RESTAURANT_NAME,
                    RESTAURANT_NIT,
                    RESTAURANT_ADDRESS,
                    RESTAURANT_PHONE,
                    RESTAURANT_LOGO_URL,
                    OWNER_ID
            );

            RestaurantResponse response = RestaurantResponse.builder()
                    .id(1L)
                    .name(RESTAURANT_NAME)
                    .nit(RESTAURANT_NIT)
                    .address(RESTAURANT_ADDRESS)
                    .phone(RESTAURANT_PHONE)
                    .logoUrl(RESTAURANT_LOGO_URL)
                    .ownerId(OWNER_ID)
                    .build();

            when(restaurantHandler.createRestaurant(any(CreateRestaurantRequest.class))).thenReturn(response);

            // Act & Assert
            mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value(1L))
                    .andExpect(jsonPath("$.name").value(RESTAURANT_NAME))
                    .andExpect(jsonPath("$.nit").value(RESTAURANT_NIT))
                    .andExpect(jsonPath("$.ownerId").value(OWNER_ID));

            verify(restaurantHandler).createRestaurant(any(CreateRestaurantRequest.class));
        }
    }

    @Nested
    @DisplayName("Create Restaurant - Validation Errors")
    class CreateRestaurantValidationErrors {

        @Test
        @DisplayName("Should return 400 when name is blank")
        void shouldReturn400WhenNameIsBlank() throws Exception {
            // Arrange
            CreateRestaurantRequest request = new CreateRestaurantRequest(
                    "",
                    RESTAURANT_NIT,
                    RESTAURANT_ADDRESS,
                    RESTAURANT_PHONE,
                    RESTAURANT_LOGO_URL,
                    OWNER_ID
            );

            // Act & Assert
            mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());

            verify(restaurantHandler, never()).createRestaurant(any());
        }

        @Test
        @DisplayName("Should return 400 when NIT is blank")
        void shouldReturn400WhenNitIsBlank() throws Exception {
            // Arrange
            CreateRestaurantRequest request = new CreateRestaurantRequest(
                    RESTAURANT_NAME,
                    "",
                    RESTAURANT_ADDRESS,
                    RESTAURANT_PHONE,
                    RESTAURANT_LOGO_URL,
                    OWNER_ID
            );

            // Act & Assert
            mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());

            verify(restaurantHandler, never()).createRestaurant(any());
        }

        @Test
        @DisplayName("Should return 400 when owner ID is null")
        void shouldReturn400WhenOwnerIdIsNull() throws Exception {
            // Arrange
            CreateRestaurantRequest request = new CreateRestaurantRequest(
                    RESTAURANT_NAME,
                    RESTAURANT_NIT,
                    RESTAURANT_ADDRESS,
                    RESTAURANT_PHONE,
                    RESTAURANT_LOGO_URL,
                    null
            );

            // Act & Assert
            mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());

            verify(restaurantHandler, never()).createRestaurant(any());
        }
    }

    @Nested
    @DisplayName("Create Restaurant - Domain Exceptions")
    class CreateRestaurantDomainExceptions {

        @Test
        @DisplayName("Should return 400 when name consists only of numbers")
        void shouldReturn400WhenNameConsistsOnlyOfNumbers() throws Exception {
            // Arrange
            CreateRestaurantRequest request = new CreateRestaurantRequest(
                    "12345",
                    RESTAURANT_NIT,
                    RESTAURANT_ADDRESS,
                    RESTAURANT_PHONE,
                    RESTAURANT_LOGO_URL,
                    OWNER_ID
            );

            when(restaurantHandler.createRestaurant(any(CreateRestaurantRequest.class)))
                    .thenThrow(new InvalidRestaurantNameException("Restaurant name cannot consist of only numbers"));

            // Act & Assert
            mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath(MESSAGE_JSON_PATH).value("Restaurant name cannot consist of only numbers"));
        }

        @Test
        @DisplayName("Should return 404 when owner does not exist")
        void shouldReturn404WhenOwnerDoesNotExist() throws Exception {
            // Arrange
            CreateRestaurantRequest request = new CreateRestaurantRequest(
                    RESTAURANT_NAME,
                    RESTAURANT_NIT,
                    RESTAURANT_ADDRESS,
                    RESTAURANT_PHONE,
                    RESTAURANT_LOGO_URL,
                    999L
            );

            when(restaurantHandler.createRestaurant(any(CreateRestaurantRequest.class)))
                    .thenThrow(new OwnerNotFoundException("User with ID 999 does not exist"));

            // Act & Assert
            mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath(MESSAGE_JSON_PATH).value("User with ID 999 does not exist"));
        }

        @Test
        @DisplayName("Should return 403 when user is not owner")
        void shouldReturn403WhenUserIsNotOwner() throws Exception {
            // Arrange
            CreateRestaurantRequest request = new CreateRestaurantRequest(
                    RESTAURANT_NAME,
                    RESTAURANT_NIT,
                    RESTAURANT_ADDRESS,
                    RESTAURANT_PHONE,
                    RESTAURANT_LOGO_URL,
                    OWNER_ID
            );

            when(restaurantHandler.createRestaurant(any(CreateRestaurantRequest.class)))
                    .thenThrow(new UserNotOwnerException("User with ID 1 does not have the OWNER role"));

            // Act & Assert
            mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath(MESSAGE_JSON_PATH).value("User with ID 1 does not have the OWNER role"));
        }

        @Test
        @DisplayName("Should return 409 when restaurant with NIT already exists")
        void shouldReturn409WhenRestaurantWithNitAlreadyExists() throws Exception {
            // Arrange
            CreateRestaurantRequest request = new CreateRestaurantRequest(
                    RESTAURANT_NAME,
                    RESTAURANT_NIT,
                    RESTAURANT_ADDRESS,
                    RESTAURANT_PHONE,
                    RESTAURANT_LOGO_URL,
                    OWNER_ID
            );

            when(restaurantHandler.createRestaurant(any(CreateRestaurantRequest.class)))
                    .thenThrow(new RestaurantAlreadyExistsException("A restaurant already exists with NIT: " + RESTAURANT_NIT));

            // Act & Assert
            mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath(MESSAGE_JSON_PATH).value("A restaurant already exists with NIT: " + RESTAURANT_NIT));
        }
    }

    @Nested
    @DisplayName("Get Dishes By Restaurant - Success Cases")
    class GetDishesByRestaurantSuccessCases {

        private static final Long RESTAURANT_ID = 1L;

        @Test
        @DisplayName("Should return paginated dishes without category filter")
        void shouldReturnPaginatedDishesWithoutCategoryFilter() throws Exception {
            // Arrange
            DishMenuItemResponseDto dish1 = DishMenuItemResponseDto.builder()
                    .id(1L)
                    .name("Hamburguesa Clásica")
                    .price(25000)
                    .description("Deliciosa hamburguesa")
                    .imageUrl("https://example.com/burger.jpg")
                    .category("Hamburguesas")
                    .build();

            DishMenuItemResponseDto dish2 = DishMenuItemResponseDto.builder()
                    .id(2L)
                    .name("Pizza Margarita")
                    .price(35000)
                    .description("Pizza tradicional")
                    .imageUrl("https://example.com/pizza.jpg")
                    .category("Pizzas")
                    .build();

            PagedResponse<DishMenuItemResponseDto> response = PagedResponse.<DishMenuItemResponseDto>builder()
                    .content(Arrays.asList(dish1, dish2))
                    .page(0)
                    .size(10)
                    .totalElements(2)
                    .totalPages(1)
                    .first(true)
                    .last(true)
                    .build();

            when(dishHandler.getDishesByRestaurant(eq(RESTAURANT_ID), eq(null), eq(0), eq(10)))
                    .thenReturn(response);

            // Act & Assert
            mockMvc.perform(get(BASE_URL + "/" + RESTAURANT_ID + "/dishes")
                            .param("page", "0")
                            .param("size", "10"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.content").isArray())
                    .andExpect(jsonPath("$.content.length()").value(2))
                    .andExpect(jsonPath("$.content[0].name").value("Hamburguesa Clásica"))
                    .andExpect(jsonPath("$.content[1].name").value("Pizza Margarita"))
                    .andExpect(jsonPath("$.page").value(0))
                    .andExpect(jsonPath("$.size").value(10))
                    .andExpect(jsonPath("$.totalElements").value(2))
                    .andExpect(jsonPath("$.totalPages").value(1))
                    .andExpect(jsonPath("$.first").value(true))
                    .andExpect(jsonPath("$.last").value(true));

            verify(dishHandler).getDishesByRestaurant(eq(RESTAURANT_ID), eq(null), eq(0), eq(10));
        }

        @Test
        @DisplayName("Should return paginated dishes with category filter")
        void shouldReturnPaginatedDishesWithCategoryFilter() throws Exception {
            // Arrange
            String category = "Hamburguesas";
            DishMenuItemResponseDto dish1 = DishMenuItemResponseDto.builder()
                    .id(1L)
                    .name("Hamburguesa Clásica")
                    .price(25000)
                    .description("Deliciosa hamburguesa")
                    .imageUrl("https://example.com/burger.jpg")
                    .category(category)
                    .build();

            PagedResponse<DishMenuItemResponseDto> response = PagedResponse.<DishMenuItemResponseDto>builder()
                    .content(List.of(dish1))
                    .page(0)
                    .size(10)
                    .totalElements(1)
                    .totalPages(1)
                    .first(true)
                    .last(true)
                    .build();

            when(dishHandler.getDishesByRestaurant(eq(RESTAURANT_ID), eq(category), eq(0), eq(10)))
                    .thenReturn(response);

            // Act & Assert
            mockMvc.perform(get(BASE_URL + "/" + RESTAURANT_ID + "/dishes")
                            .param("category", category)
                            .param("page", "0")
                            .param("size", "10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content.length()").value(1))
                    .andExpect(jsonPath("$.content[0].category").value(category));

            verify(dishHandler).getDishesByRestaurant(eq(RESTAURANT_ID), eq(category), eq(0), eq(10));
        }

        @Test
        @DisplayName("Should return empty list when no dishes found")
        void shouldReturnEmptyListWhenNoDishesFound() throws Exception {
            // Arrange
            PagedResponse<DishMenuItemResponseDto> emptyResponse = PagedResponse.<DishMenuItemResponseDto>builder()
                    .content(List.of())
                    .page(0)
                    .size(10)
                    .totalElements(0)
                    .totalPages(0)
                    .first(true)
                    .last(true)
                    .build();

            when(dishHandler.getDishesByRestaurant(eq(RESTAURANT_ID), eq(null), eq(0), eq(10)))
                    .thenReturn(emptyResponse);

            // Act & Assert
            mockMvc.perform(get(BASE_URL + "/" + RESTAURANT_ID + "/dishes")
                            .param("page", "0")
                            .param("size", "10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isArray())
                    .andExpect(jsonPath("$.content.length()").value(0))
                    .andExpect(jsonPath("$.totalElements").value(0));

            verify(dishHandler).getDishesByRestaurant(eq(RESTAURANT_ID), eq(null), eq(0), eq(10));
        }

        @Test
        @DisplayName("Should use default pagination values when not provided")
        void shouldUseDefaultPaginationValuesWhenNotProvided() throws Exception {
            // Arrange
            PagedResponse<DishMenuItemResponseDto> response = PagedResponse.<DishMenuItemResponseDto>builder()
                    .content(List.of())
                    .page(0)
                    .size(10)
                    .totalElements(0)
                    .totalPages(0)
                    .first(true)
                    .last(true)
                    .build();

            when(dishHandler.getDishesByRestaurant(eq(RESTAURANT_ID), eq(null), eq(0), eq(10)))
                    .thenReturn(response);

            // Act & Assert
            mockMvc.perform(get(BASE_URL + "/" + RESTAURANT_ID + "/dishes"))
                    .andExpect(status().isOk());

            verify(dishHandler).getDishesByRestaurant(eq(RESTAURANT_ID), eq(null), eq(0), eq(10));
        }
    }

    @Nested
    @DisplayName("Get Dishes By Restaurant - Error Cases")
    class GetDishesByRestaurantErrorCases {

        private static final Long RESTAURANT_ID = 999L;

        @Test
        @DisplayName("Should return 404 when restaurant does not exist")
        void shouldReturn404WhenRestaurantDoesNotExist() throws Exception {
            // Arrange
            when(dishHandler.getDishesByRestaurant(eq(RESTAURANT_ID), eq(null), eq(0), eq(10)))
                    .thenThrow(new RestaurantNotFoundException(RESTAURANT_ID));

            // Act & Assert
            mockMvc.perform(get(BASE_URL + "/" + RESTAURANT_ID + "/dishes")
                            .param("page", "0")
                            .param("size", "10"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath(MESSAGE_JSON_PATH).value("Restaurant not found with id: " + RESTAURANT_ID));
        }
    }
}
