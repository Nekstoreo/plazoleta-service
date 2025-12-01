package com.pragma.plazoleta.application.handler;

import com.pragma.plazoleta.application.dto.request.CreateRestaurantRequest;
import com.pragma.plazoleta.application.dto.response.RestaurantResponse;
import com.pragma.plazoleta.application.mapper.RestaurantRequestMapper;
import com.pragma.plazoleta.application.mapper.RestaurantResponseMapper;
import com.pragma.plazoleta.domain.api.IRestaurantServicePort;
import com.pragma.plazoleta.domain.model.Restaurant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
}
