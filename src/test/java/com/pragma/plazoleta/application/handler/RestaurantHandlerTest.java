package com.pragma.plazoleta.application.handler;

import com.pragma.plazoleta.application.dto.request.CreateRestaurantRequest;
import com.pragma.plazoleta.application.dto.response.PagedResponse;
import com.pragma.plazoleta.application.dto.response.RestaurantListItemResponse;
import com.pragma.plazoleta.application.dto.response.RestaurantResponse;
import com.pragma.plazoleta.application.mapper.RestaurantRequestMapper;
import com.pragma.plazoleta.application.mapper.RestaurantResponseMapper;
import com.pragma.plazoleta.domain.api.IRestaurantServicePort;
import com.pragma.plazoleta.domain.model.PagedResult;
import com.pragma.plazoleta.domain.model.Restaurant;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RestaurantHandlerTest {

    private static final Long OWNER_ID = 1L;
    private static final String RESTAURANT_NAME = "El Buen Sabor";
    private static final String RESTAURANT_NIT = "123456789";
    private static final String RESTAURANT_ADDRESS = "Calle 123 #45-67";
    private static final String RESTAURANT_PHONE = "+573001234567";
    private static final String RESTAURANT_LOGO_URL = "https://example.com/logo.png";
    private static final String RESTAURANT_LOGO_ALITAS = "https://example.com/alitas.png";
    private static final String RESTAURANT_LOGO_BURGER = "https://example.com/burger.png";
    private static final String RESTAURANT_NAME_ALITAS = "Alitas Locas";
    private static final String RESTAURANT_NAME_BURGER = "Burger King";

    @Mock
    private IRestaurantServicePort restaurantServicePort;

    @Mock
    private RestaurantRequestMapper restaurantRequestMapper;

    @Mock
    private RestaurantResponseMapper restaurantResponseMapper;

    @InjectMocks
    private RestaurantHandler restaurantHandler;

    private CreateRestaurantRequest createRestaurantRequest;
    private Restaurant restaurant;
    private Restaurant savedRestaurant;
    private RestaurantResponse restaurantResponse;

    @BeforeEach
    void setUp() {
        createRestaurantRequest = new CreateRestaurantRequest(
                RESTAURANT_NAME,
                RESTAURANT_NIT,
                RESTAURANT_ADDRESS,
                RESTAURANT_PHONE,
                RESTAURANT_LOGO_URL,
                OWNER_ID
        );

        restaurant = new Restaurant();
        restaurant.setName(RESTAURANT_NAME);
        restaurant.setNit(RESTAURANT_NIT);
        restaurant.setAddress(RESTAURANT_ADDRESS);
        restaurant.setPhone(RESTAURANT_PHONE);
        restaurant.setLogoUrl(RESTAURANT_LOGO_URL);
        restaurant.setOwnerId(OWNER_ID);

        savedRestaurant = new Restaurant();
        savedRestaurant.setId(1L);
        savedRestaurant.setName(RESTAURANT_NAME);
        savedRestaurant.setNit(RESTAURANT_NIT);
        savedRestaurant.setAddress(RESTAURANT_ADDRESS);
        savedRestaurant.setPhone(RESTAURANT_PHONE);
        savedRestaurant.setLogoUrl(RESTAURANT_LOGO_URL);
        savedRestaurant.setOwnerId(OWNER_ID);

        restaurantResponse = RestaurantResponse.builder()
                .id(1L)
                .name(RESTAURANT_NAME)
                .nit(RESTAURANT_NIT)
                .address(RESTAURANT_ADDRESS)
                .phone(RESTAURANT_PHONE)
                .logoUrl(RESTAURANT_LOGO_URL)
                .ownerId(OWNER_ID)
                .build();
    }

    @Test
    @DisplayName("Should create restaurant and return response")
    void shouldCreateRestaurantAndReturnResponse() {
        // Arrange
        when(restaurantRequestMapper.toRestaurant(createRestaurantRequest)).thenReturn(restaurant);
        when(restaurantServicePort.createRestaurant(restaurant)).thenReturn(savedRestaurant);
        when(restaurantResponseMapper.toResponse(savedRestaurant)).thenReturn(restaurantResponse);

        // Act
        RestaurantResponse result = restaurantHandler.createRestaurant(createRestaurantRequest);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(RESTAURANT_NAME, result.getName());
        assertEquals(RESTAURANT_NIT, result.getNit());

        verify(restaurantRequestMapper).toRestaurant(createRestaurantRequest);
        verify(restaurantServicePort).createRestaurant(restaurant);
        verify(restaurantResponseMapper).toResponse(savedRestaurant);
    }

    @Test
    @DisplayName("Should call mapper to convert request to domain model")
    void shouldCallMapperToConvertRequestToDomainModel() {
        // Arrange
        when(restaurantRequestMapper.toRestaurant(any(CreateRestaurantRequest.class))).thenReturn(restaurant);
        when(restaurantServicePort.createRestaurant(any(Restaurant.class))).thenReturn(savedRestaurant);
        when(restaurantResponseMapper.toResponse(any(Restaurant.class))).thenReturn(restaurantResponse);

        // Act
        restaurantHandler.createRestaurant(createRestaurantRequest);

        // Assert
        verify(restaurantRequestMapper).toRestaurant(createRestaurantRequest);
    }

    @Test
    @DisplayName("Should call service port to create restaurant")
    void shouldCallServicePortToCreateRestaurant() {
        // Arrange
        when(restaurantRequestMapper.toRestaurant(any(CreateRestaurantRequest.class))).thenReturn(restaurant);
        when(restaurantServicePort.createRestaurant(any(Restaurant.class))).thenReturn(savedRestaurant);
        when(restaurantResponseMapper.toResponse(any(Restaurant.class))).thenReturn(restaurantResponse);

        // Act
        restaurantHandler.createRestaurant(createRestaurantRequest);

        // Assert
        verify(restaurantServicePort).createRestaurant(restaurant);
    }

    @Test
    @DisplayName("Should call mapper to convert saved restaurant to response")
    void shouldCallMapperToConvertSavedRestaurantToResponse() {
        // Arrange
        when(restaurantRequestMapper.toRestaurant(any(CreateRestaurantRequest.class))).thenReturn(restaurant);
        when(restaurantServicePort.createRestaurant(any(Restaurant.class))).thenReturn(savedRestaurant);
        when(restaurantResponseMapper.toResponse(any(Restaurant.class))).thenReturn(restaurantResponse);

        // Act
        restaurantHandler.createRestaurant(createRestaurantRequest);

        // Assert
        verify(restaurantResponseMapper).toResponse(savedRestaurant);
    }

    @Nested
    @DisplayName("Get All Restaurants")
    class GetAllRestaurantsTests {

        @Test
        @DisplayName("Should return paginated restaurant list items")
        void shouldReturnPaginatedRestaurantListItems() {
            // Arrange
            Restaurant restaurant1 = new Restaurant();
            restaurant1.setId(1L);
            restaurant1.setName(RESTAURANT_NAME_ALITAS);
            restaurant1.setLogoUrl(RESTAURANT_LOGO_ALITAS);

            Restaurant restaurant2 = new Restaurant();
            restaurant2.setId(2L);
            restaurant2.setName(RESTAURANT_NAME_BURGER);
            restaurant2.setLogoUrl(RESTAURANT_LOGO_BURGER);

            List<Restaurant> restaurants = Arrays.asList(restaurant1, restaurant2);
            PagedResult<Restaurant> pagedResult = PagedResult.of(restaurants, 0, 10, 2, 1);

            RestaurantListItemResponse item1 = RestaurantListItemResponse.builder()
                    .name(RESTAURANT_NAME_ALITAS)
                    .logoUrl(RESTAURANT_LOGO_ALITAS)
                    .build();

            RestaurantListItemResponse item2 = RestaurantListItemResponse.builder()
                    .name(RESTAURANT_NAME_BURGER)
                    .logoUrl(RESTAURANT_LOGO_BURGER)
                    .build();

            when(restaurantServicePort.getAllRestaurants(0, 10)).thenReturn(pagedResult);
            when(restaurantResponseMapper.toListItemResponse(restaurant1)).thenReturn(item1);
            when(restaurantResponseMapper.toListItemResponse(restaurant2)).thenReturn(item2);

            // Act
            PagedResponse<RestaurantListItemResponse> result = restaurantHandler.getAllRestaurants(0, 10);

            // Assert
            assertNotNull(result);
            assertEquals(2, result.getContent().size());
            assertEquals(RESTAURANT_NAME_ALITAS, result.getContent().get(0).getName());
            assertEquals(RESTAURANT_LOGO_ALITAS, result.getContent().get(0).getLogoUrl());
            assertEquals(RESTAURANT_NAME_BURGER, result.getContent().get(1).getName());
            assertEquals(RESTAURANT_LOGO_BURGER, result.getContent().get(1).getLogoUrl());
            assertEquals(0, result.getPage());
            assertEquals(10, result.getSize());
            assertEquals(2, result.getTotalElements());
            assertEquals(1, result.getTotalPages());
            assertTrue(result.isFirst());
            assertTrue(result.isLast());

            verify(restaurantServicePort).getAllRestaurants(0, 10);
            verify(restaurantResponseMapper).toListItemResponse(restaurant1);
            verify(restaurantResponseMapper).toListItemResponse(restaurant2);
        }

        @Test
        @DisplayName("Should return empty paged response when no restaurants exist")
        void shouldReturnEmptyPagedResponseWhenNoRestaurantsExist() {
            // Arrange
            PagedResult<Restaurant> pagedResult = PagedResult.of(List.of(), 0, 10, 0, 0);
            when(restaurantServicePort.getAllRestaurants(0, 10)).thenReturn(pagedResult);

            // Act
            PagedResponse<RestaurantListItemResponse> result = restaurantHandler.getAllRestaurants(0, 10);

            // Assert
            assertNotNull(result);
            assertTrue(result.getContent().isEmpty());
            assertEquals(0, result.getTotalElements());
            assertEquals(0, result.getTotalPages());
        }

        @Test
        @DisplayName("Should correctly pass pagination parameters to service")
        void shouldCorrectlyPassPaginationParametersToService() {
            // Arrange
            PagedResult<Restaurant> pagedResult = PagedResult.of(List.of(), 2, 5, 15, 3);
            when(restaurantServicePort.getAllRestaurants(2, 5)).thenReturn(pagedResult);

            // Act
            PagedResponse<RestaurantListItemResponse> result = restaurantHandler.getAllRestaurants(2, 5);

            // Assert
            assertNotNull(result);
            assertEquals(2, result.getPage());
            assertEquals(5, result.getSize());
            assertEquals(15, result.getTotalElements());
            assertEquals(3, result.getTotalPages());
            assertFalse(result.isFirst());
            assertTrue(result.isLast());

            verify(restaurantServicePort).getAllRestaurants(2, 5);
        }
    }
}
