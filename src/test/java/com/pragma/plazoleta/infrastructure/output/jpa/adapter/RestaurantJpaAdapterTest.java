package com.pragma.plazoleta.infrastructure.output.jpa.adapter;

import com.pragma.plazoleta.domain.model.Restaurant;
import com.pragma.plazoleta.infrastructure.output.jpa.entity.RestaurantEntity;
import com.pragma.plazoleta.infrastructure.output.jpa.mapper.RestaurantEntityMapper;
import com.pragma.plazoleta.infrastructure.output.jpa.repository.IRestaurantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RestaurantJpaAdapterTest {

    private static final Long OWNER_ID = 1L;
    private static final String RESTAURANT_NAME = "El Buen Sabor";
    private static final String RESTAURANT_NIT = "123456789";

    @Mock
    private IRestaurantRepository restaurantRepository;

    @Mock
    private RestaurantEntityMapper restaurantEntityMapper;

    @InjectMocks
    private RestaurantJpaAdapter restaurantJpaAdapter;

    private Restaurant restaurant;
    private RestaurantEntity restaurantEntity;
    private RestaurantEntity savedEntity;

    @BeforeEach
    void setUp() {
        restaurant = new Restaurant();
        restaurant.setName(RESTAURANT_NAME);
        restaurant.setNit(RESTAURANT_NIT);
        restaurant.setAddress("Calle 123 #45-67");
        restaurant.setPhone("+573001234567");
        restaurant.setLogoUrl("https://example.com/logo.png");
        restaurant.setOwnerId(OWNER_ID);

        restaurantEntity = new RestaurantEntity();
        restaurantEntity.setName(RESTAURANT_NAME);
        restaurantEntity.setNit(RESTAURANT_NIT);
        restaurantEntity.setAddress("Calle 123 #45-67");
        restaurantEntity.setPhone("+573001234567");
        restaurantEntity.setLogoUrl("https://example.com/logo.png");
        restaurantEntity.setOwnerId(OWNER_ID);

        savedEntity = new RestaurantEntity();
        savedEntity.setId(1L);
        savedEntity.setName(RESTAURANT_NAME);
        savedEntity.setNit(RESTAURANT_NIT);
        savedEntity.setAddress("Calle 123 #45-67");
        savedEntity.setPhone("+573001234567");
        savedEntity.setLogoUrl("https://example.com/logo.png");
        savedEntity.setOwnerId(OWNER_ID);
    }

    @Nested
    @DisplayName("Save Restaurant Tests")
    class SaveRestaurantTests {

        @Test
        @DisplayName("Should save restaurant successfully")
        void shouldSaveRestaurantSuccessfully() {
            // Arrange
            Restaurant savedRestaurant = new Restaurant();
            savedRestaurant.setId(1L);
            savedRestaurant.setName(RESTAURANT_NAME);
            savedRestaurant.setNit(RESTAURANT_NIT);

            when(restaurantEntityMapper.toEntity(restaurant)).thenReturn(restaurantEntity);
            when(restaurantRepository.save(restaurantEntity)).thenReturn(savedEntity);
            when(restaurantEntityMapper.toRestaurant(savedEntity)).thenReturn(savedRestaurant);

            // Act
            Restaurant result = restaurantJpaAdapter.saveRestaurant(restaurant);

            // Assert
            assertNotNull(result);
            assertEquals(1L, result.getId());
            verify(restaurantRepository).save(restaurantEntity);
        }

        @Test
        @DisplayName("Should call mapper to convert domain to entity")
        void shouldCallMapperToConvertDomainToEntity() {
            // Arrange
            when(restaurantEntityMapper.toEntity(any(Restaurant.class))).thenReturn(restaurantEntity);
            when(restaurantRepository.save(any(RestaurantEntity.class))).thenReturn(savedEntity);
            when(restaurantEntityMapper.toRestaurant(any(RestaurantEntity.class))).thenReturn(restaurant);

            // Act
            restaurantJpaAdapter.saveRestaurant(restaurant);

            // Assert
            verify(restaurantEntityMapper).toEntity(restaurant);
        }
    }

    @Nested
    @DisplayName("Exists By NIT Tests")
    class ExistsByNitTests {

        @Test
        @DisplayName("Should return true when restaurant with NIT exists")
        void shouldReturnTrueWhenRestaurantWithNitExists() {
            // Arrange
            when(restaurantRepository.existsByNit(RESTAURANT_NIT)).thenReturn(true);

            // Act
            boolean result = restaurantJpaAdapter.existsByNit(RESTAURANT_NIT);

            // Assert
            assertTrue(result);
            verify(restaurantRepository).existsByNit(RESTAURANT_NIT);
        }

        @Test
        @DisplayName("Should return false when restaurant with NIT does not exist")
        void shouldReturnFalseWhenRestaurantWithNitDoesNotExist() {
            // Arrange
            when(restaurantRepository.existsByNit(RESTAURANT_NIT)).thenReturn(false);

            // Act
            boolean result = restaurantJpaAdapter.existsByNit(RESTAURANT_NIT);

            // Assert
            assertFalse(result);
            verify(restaurantRepository).existsByNit(RESTAURANT_NIT);
        }
    }
}
