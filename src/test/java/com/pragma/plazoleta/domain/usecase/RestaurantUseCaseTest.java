package com.pragma.plazoleta.domain.usecase;

import com.pragma.plazoleta.domain.exception.*;
import com.pragma.plazoleta.domain.model.PagedResult;
import com.pragma.plazoleta.domain.model.Restaurant;
import com.pragma.plazoleta.domain.spi.IRestaurantPersistencePort;
import com.pragma.plazoleta.domain.spi.IUserValidationPort;
import com.pragma.plazoleta.infrastructure.constant.SecurityConstants;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RestaurantUseCaseTest {

    private static final Long VALID_OWNER_ID = 1L;

    @Mock
    private IRestaurantPersistencePort restaurantPersistencePort;

    @Mock
    private IUserValidationPort userValidationPort;

    @InjectMocks
    private RestaurantUseCase restaurantUseCase;

    private Restaurant validRestaurant;

    @BeforeEach
    void setUp() {
        validRestaurant = new Restaurant();
        validRestaurant.setName("El Buen Sabor");
        validRestaurant.setNit("123456789");
        validRestaurant.setAddress("Calle 123 #45-67");
        validRestaurant.setPhone("+573001234567");
        validRestaurant.setLogoUrl("https://example.com/logo.png");
        validRestaurant.setOwnerId(VALID_OWNER_ID);
    }

    @Nested
    @DisplayName("Create Restaurant - Success Cases")
    class CreateRestaurantSuccessCases {

        @Test
        @DisplayName("Should create restaurant successfully with valid data")
        void shouldCreateRestaurantSuccessfully() {
            // Arrange
            when(userValidationPort.getUserRoleById(VALID_OWNER_ID)).thenReturn(Optional.of(SecurityConstants.ROLE_OWNER));
            when(restaurantPersistencePort.existsByNit(anyString())).thenReturn(false);
            when(restaurantPersistencePort.saveRestaurant(any(Restaurant.class))).thenAnswer(invocation -> {
                Restaurant restaurant = invocation.getArgument(0);
                restaurant.setId(1L);
                return restaurant;
            });

            // Act
            Restaurant result = restaurantUseCase.createRestaurant(validRestaurant);

            // Assert
            assertNotNull(result);
            assertNotNull(result.getId());
            assertEquals("El Buen Sabor", result.getName());
            verify(restaurantPersistencePort).saveRestaurant(any(Restaurant.class));
        }

        @Test
        @DisplayName("Should create restaurant with name containing numbers")
        void shouldCreateRestaurantWithNameContainingNumbers() {
            // Arrange
            validRestaurant.setName("Restaurant 24h");
            when(userValidationPort.getUserRoleById(VALID_OWNER_ID)).thenReturn(Optional.of(SecurityConstants.ROLE_OWNER));
            when(restaurantPersistencePort.existsByNit(anyString())).thenReturn(false);
            when(restaurantPersistencePort.saveRestaurant(any(Restaurant.class))).thenReturn(validRestaurant);

            // Act
            Restaurant result = restaurantUseCase.createRestaurant(validRestaurant);

            // Assert
            assertNotNull(result);
            assertEquals("Restaurant 24h", result.getName());
        }

        @Test
        @DisplayName("Should create restaurant with phone containing + symbol")
        void shouldCreateRestaurantWithPhoneContainingPlusSymbol() {
            // Arrange
            validRestaurant.setPhone("+573005698325");
            when(userValidationPort.getUserRoleById(VALID_OWNER_ID)).thenReturn(Optional.of(SecurityConstants.ROLE_OWNER));
            when(restaurantPersistencePort.existsByNit(anyString())).thenReturn(false);
            when(restaurantPersistencePort.saveRestaurant(any(Restaurant.class))).thenReturn(validRestaurant);

            // Act
            Restaurant result = restaurantUseCase.createRestaurant(validRestaurant);

            // Assert
            assertNotNull(result);
            verify(restaurantPersistencePort).saveRestaurant(any(Restaurant.class));
        }
    }

    @Nested
    @DisplayName("Create Restaurant - Name Validation")
    class NameValidationTests {

        @Test
        @DisplayName("Should throw exception when name is null")
        void shouldThrowExceptionWhenNameIsNull() {
            // Arrange
            validRestaurant.setName(null);

            // Act & Assert
            assertThrows(InvalidRestaurantNameException.class,
                    () -> restaurantUseCase.createRestaurant(validRestaurant));
            verify(restaurantPersistencePort, never()).saveRestaurant(any(Restaurant.class));
        }

        @Test
        @DisplayName("Should throw exception when name is blank")
        void shouldThrowExceptionWhenNameIsBlank() {
            // Arrange
            validRestaurant.setName("   ");

            // Act & Assert
            assertThrows(InvalidRestaurantNameException.class,
                    () -> restaurantUseCase.createRestaurant(validRestaurant));
            verify(restaurantPersistencePort, never()).saveRestaurant(any(Restaurant.class));
        }

        @Test
        @DisplayName("Should throw exception when name consists only of numbers")
        void shouldThrowExceptionWhenNameConsistsOnlyOfNumbers() {
            // Arrange
            validRestaurant.setName("12345");

            // Act & Assert
            InvalidRestaurantNameException exception = assertThrows(
                    InvalidRestaurantNameException.class,
                    () -> restaurantUseCase.createRestaurant(validRestaurant));
            assertTrue(exception.getMessage().contains("cannot consist of only numbers"));
        }
    }

    @Nested
    @DisplayName("Create Restaurant - NIT Validation")
    class NitValidationTests {

        @Test
        @DisplayName("Should throw exception when NIT is null")
        void shouldThrowExceptionWhenNitIsNull() {
            // Arrange
            validRestaurant.setNit(null);

            // Act & Assert
            assertThrows(InvalidNitException.class,
                    () -> restaurantUseCase.createRestaurant(validRestaurant));
            verify(restaurantPersistencePort, never()).saveRestaurant(any(Restaurant.class));
        }

        @Test
        @DisplayName("Should throw exception when NIT contains non-numeric characters")
        void shouldThrowExceptionWhenNitContainsNonNumericCharacters() {
            // Arrange
            validRestaurant.setNit("ABC123XYZ");

            // Act & Assert
            InvalidNitException exception = assertThrows(
                    InvalidNitException.class,
                    () -> restaurantUseCase.createRestaurant(validRestaurant));
            assertTrue(exception.getMessage().contains("numeric only"));
        }

        @Test
        @DisplayName("Should throw exception when NIT contains special characters")
        void shouldThrowExceptionWhenNitContainsSpecialCharacters() {
            // Arrange
            validRestaurant.setNit("123-456-789");

            // Act & Assert
            assertThrows(InvalidNitException.class,
                    () -> restaurantUseCase.createRestaurant(validRestaurant));
        }
    }

    @Nested
    @DisplayName("Create Restaurant - Phone Validation")
    class PhoneValidationTests {

        @Test
        @DisplayName("Should throw exception when phone is null")
        void shouldThrowExceptionWhenPhoneIsNull() {
            // Arrange
            validRestaurant.setPhone(null);

            // Act & Assert
            assertThrows(InvalidPhoneException.class,
                    () -> restaurantUseCase.createRestaurant(validRestaurant));
            verify(restaurantPersistencePort, never()).saveRestaurant(any(Restaurant.class));
        }

        @Test
        @DisplayName("Should throw exception when phone exceeds 13 characters")
        void shouldThrowExceptionWhenPhoneExceeds13Characters() {
            // Arrange
            validRestaurant.setPhone("+5730012345678901");

            // Act & Assert
            InvalidPhoneException exception = assertThrows(
                    InvalidPhoneException.class,
                    () -> restaurantUseCase.createRestaurant(validRestaurant));
            assertTrue(exception.getMessage().contains("maximum of 13 characters"));
        }

        @Test
        @DisplayName("Should throw exception when phone contains invalid characters")
        void shouldThrowExceptionWhenPhoneContainsInvalidCharacters() {
            // Arrange
            validRestaurant.setPhone("300-123-4567");

            // Act & Assert
            assertThrows(InvalidPhoneException.class,
                    () -> restaurantUseCase.createRestaurant(validRestaurant));
        }
    }

    @Nested
    @DisplayName("Create Restaurant - Owner Validation")
    class OwnerValidationTests {

        @Test
        @DisplayName("Should throw exception when owner ID is null")
        void shouldThrowExceptionWhenOwnerIdIsNull() {
            // Arrange
            validRestaurant.setOwnerId(null);

            // Act & Assert
            assertThrows(OwnerNotFoundException.class,
                    () -> restaurantUseCase.createRestaurant(validRestaurant));
            verify(restaurantPersistencePort, never()).saveRestaurant(any(Restaurant.class));
        }

        @Test
        @DisplayName("Should throw exception when owner does not exist")
        void shouldThrowExceptionWhenOwnerDoesNotExist() {
            // Arrange
            when(userValidationPort.getUserRoleById(anyLong())).thenReturn(Optional.empty());

            // Act & Assert
            OwnerNotFoundException exception = assertThrows(
                    OwnerNotFoundException.class,
                    () -> restaurantUseCase.createRestaurant(validRestaurant));
            assertTrue(exception.getMessage().contains("does not exist"));
        }

        @Test
        @DisplayName("Should throw exception when user does not have OWNER role")
        void shouldThrowExceptionWhenUserDoesNotHaveOwnerRole() {
            // Arrange
            when(userValidationPort.getUserRoleById(VALID_OWNER_ID)).thenReturn(Optional.of("CLIENT"));

            // Act & Assert
            UserNotOwnerException exception = assertThrows(
                    UserNotOwnerException.class,
                    () -> restaurantUseCase.createRestaurant(validRestaurant));
            assertTrue(exception.getMessage().contains("does not have the OWNER role"));
        }
    }

    @Nested
    @DisplayName("Create Restaurant - Duplicate Validation")
    class DuplicateValidationTests {

        @Test
        @DisplayName("Should throw exception when restaurant with NIT already exists")
        void shouldThrowExceptionWhenRestaurantWithNitAlreadyExists() {
            // Arrange
            when(userValidationPort.getUserRoleById(VALID_OWNER_ID)).thenReturn(Optional.of(SecurityConstants.ROLE_OWNER));
            when(restaurantPersistencePort.existsByNit(validRestaurant.getNit())).thenReturn(true);

            // Act & Assert
            RestaurantAlreadyExistsException exception = assertThrows(
                    RestaurantAlreadyExistsException.class,
                    () -> restaurantUseCase.createRestaurant(validRestaurant));
            assertTrue(exception.getMessage().contains("already exists with NIT"));
            verify(restaurantPersistencePort, never()).saveRestaurant(any(Restaurant.class));
        }
    }

    @Nested
    @DisplayName("Get All Restaurants - Pagination")
    class GetAllRestaurantsTests {

        @Test
        @DisplayName("Should return paginated restaurants ordered alphabetically")
        void shouldReturnPaginatedRestaurantsOrderedAlphabetically() {
            // Arrange
            Restaurant restaurant1 = new Restaurant();
            restaurant1.setId(1L);
            restaurant1.setName("Alitas Locas");
            restaurant1.setLogoUrl("https://example.com/alitas.png");

            Restaurant restaurant2 = new Restaurant();
            restaurant2.setId(2L);
            restaurant2.setName("Burger King");
            restaurant2.setLogoUrl("https://example.com/burger.png");

            List<Restaurant> restaurants = Arrays.asList(restaurant1, restaurant2);
            PagedResult<Restaurant> pagedResult = PagedResult.of(restaurants, 0, 10, 2, 1);

            when(restaurantPersistencePort.findAllOrderedByNamePaginated(0, 10)).thenReturn(pagedResult);

            // Act
            PagedResult<Restaurant> result = restaurantUseCase.getAllRestaurants(0, 10);

            // Assert
            assertNotNull(result);
            assertEquals(2, result.getContent().size());
            assertEquals("Alitas Locas", result.getContent().get(0).getName());
            assertEquals("Burger King", result.getContent().get(1).getName());
            assertEquals(0, result.getPage());
            assertEquals(10, result.getSize());
            assertEquals(2, result.getTotalElements());
            assertEquals(1, result.getTotalPages());
            assertTrue(result.isFirst());
            assertTrue(result.isLast());

            verify(restaurantPersistencePort).findAllOrderedByNamePaginated(0, 10);
        }

        @Test
        @DisplayName("Should return empty page when no restaurants exist")
        void shouldReturnEmptyPageWhenNoRestaurantsExist() {
            // Arrange
            PagedResult<Restaurant> pagedResult = PagedResult.of(List.of(), 0, 10, 0, 0);
            when(restaurantPersistencePort.findAllOrderedByNamePaginated(0, 10)).thenReturn(pagedResult);

            // Act
            PagedResult<Restaurant> result = restaurantUseCase.getAllRestaurants(0, 10);

            // Assert
            assertNotNull(result);
            assertTrue(result.getContent().isEmpty());
            assertEquals(0, result.getTotalElements());
        }

        @Test
        @DisplayName("Should correctly paginate with different page sizes")
        void shouldCorrectlyPaginateWithDifferentPageSizes() {
            // Arrange
            Restaurant restaurant = new Restaurant();
            restaurant.setId(1L);
            restaurant.setName("Test Restaurant");
            restaurant.setLogoUrl("https://example.com/test.png");

            PagedResult<Restaurant> pagedResult = PagedResult.of(List.of(restaurant), 1, 5, 10, 2);
            when(restaurantPersistencePort.findAllOrderedByNamePaginated(1, 5)).thenReturn(pagedResult);

            // Act
            PagedResult<Restaurant> result = restaurantUseCase.getAllRestaurants(1, 5);

            // Assert
            assertNotNull(result);
            assertEquals(1, result.getContent().size());
            assertEquals(1, result.getPage());
            assertEquals(5, result.getSize());
            assertEquals(10, result.getTotalElements());
            assertEquals(2, result.getTotalPages());
            assertFalse(result.isFirst());
            assertTrue(result.isLast());
        }
    }
}
