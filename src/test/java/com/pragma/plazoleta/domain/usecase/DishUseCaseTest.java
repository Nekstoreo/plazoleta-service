package com.pragma.plazoleta.domain.usecase;

import com.pragma.plazoleta.domain.exception.DishNotFoundException;
import com.pragma.plazoleta.domain.exception.InvalidActiveStatusException;
import com.pragma.plazoleta.domain.exception.InvalidPriceException;
import com.pragma.plazoleta.domain.exception.RestaurantNotFoundException;
import com.pragma.plazoleta.domain.exception.UserNotRestaurantOwnerException;
import com.pragma.plazoleta.domain.model.Dish;
import com.pragma.plazoleta.domain.model.Restaurant;
import com.pragma.plazoleta.domain.spi.IDishPersistencePort;
import com.pragma.plazoleta.domain.spi.IRestaurantPersistencePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DishUseCaseTest {

    @Mock
    private IDishPersistencePort dishPersistencePort;

    @Mock
    private IRestaurantPersistencePort restaurantPersistencePort;

    @InjectMocks
    private DishUseCase dishUseCase;

    private Dish validDish;
    private Restaurant restaurant;
    private static final Long DISH_ID = 1L;
    private static final Long OWNER_ID = 1L;
    private static final Long RESTAURANT_ID = 10L;

    @BeforeEach
    void setUp() {
        validDish = new Dish(
                "Hamburguesa Clásica",
                25000,
                "Deliciosa hamburguesa con carne 100% res",
                "https://example.com/burger.jpg",
                "Hamburguesas",
                RESTAURANT_ID
        );

        restaurant = new Restaurant(
                "Mi Restaurante",
                "123456789",
                "Calle 123",
                "+573001234567",
                "https://example.com/logo.jpg",
                OWNER_ID
        );
        restaurant.setId(RESTAURANT_ID);
    }

    @Nested
    @DisplayName("Create Dish - Happy Path")
    class CreateDishHappyPath {

        @Test
        @DisplayName("Should create dish successfully when all validations pass")
        void shouldCreateDishSuccessfully() {
            Dish savedDish = new Dish(
                    validDish.getName(),
                    validDish.getPrice(),
                    validDish.getDescription(),
                    validDish.getImageUrl(),
                    validDish.getCategory(),
                    RESTAURANT_ID
            );
            savedDish.setId(1L);
            savedDish.setActive(true);

            when(restaurantPersistencePort.findById(RESTAURANT_ID)).thenReturn(Optional.of(restaurant));
            when(dishPersistencePort.saveDish(any(Dish.class))).thenReturn(savedDish);

            Dish result = dishUseCase.createDish(validDish, OWNER_ID);

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getName()).isEqualTo("Hamburguesa Clásica");
            assertThat(result.getPrice()).isEqualTo(25000);
            assertThat(result.getActive()).isTrue();

            verify(restaurantPersistencePort).findById(RESTAURANT_ID);
            verify(dishPersistencePort).saveDish(any(Dish.class));
        }

        @Test
        @DisplayName("Should set active to true when creating dish")
        void shouldSetActiveToTrueWhenCreating() {
            validDish.setActive(null);

            when(restaurantPersistencePort.findById(RESTAURANT_ID)).thenReturn(Optional.of(restaurant));
            when(dishPersistencePort.saveDish(any(Dish.class))).thenAnswer(invocation -> {
                Dish dish = invocation.getArgument(0);
                assertThat(dish.getActive()).isTrue();
                return dish;
            });

            dishUseCase.createDish(validDish, OWNER_ID);

            verify(dishPersistencePort).saveDish(argThat(dish -> dish.getActive() == Boolean.TRUE));
        }
    }

    @Nested
    @DisplayName("Create Dish - Price Validation")
    class CreateDishPriceValidation {

        @ParameterizedTest
        @ValueSource(ints = {0, -1, -100, -999})
        @DisplayName("Should throw InvalidPriceException when price is not positive")
        void shouldThrowExceptionWhenPriceIsNotPositive(int invalidPrice) {
            validDish.setPrice(invalidPrice);

            assertThatThrownBy(() -> dishUseCase.createDish(validDish, OWNER_ID))
                    .isInstanceOf(InvalidPriceException.class)
                    .hasMessageContaining("positive");

            verify(restaurantPersistencePort, never()).findById(any());
            verify(dishPersistencePort, never()).saveDish(any());
        }

        @Test
        @DisplayName("Should throw InvalidPriceException when price is null")
        void shouldThrowExceptionWhenPriceIsNull() {
            validDish.setPrice(null);

            assertThatThrownBy(() -> dishUseCase.createDish(validDish, OWNER_ID))
                    .isInstanceOf(InvalidPriceException.class);

            verify(dishPersistencePort, never()).saveDish(any());
        }
    }

    @Nested
    @DisplayName("Create Dish - Restaurant Validation")
    class CreateDishRestaurantValidation {

        @Test
        @DisplayName("Should throw RestaurantNotFoundException when restaurant does not exist")
        void shouldThrowExceptionWhenRestaurantNotFound() {
            when(restaurantPersistencePort.findById(RESTAURANT_ID)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> dishUseCase.createDish(validDish, OWNER_ID))
                    .isInstanceOf(RestaurantNotFoundException.class)
                    .hasMessageContaining(RESTAURANT_ID.toString());

            verify(dishPersistencePort, never()).saveDish(any());
        }
    }

    @Nested
    @DisplayName("Create Dish - Ownership Validation")
    class CreateDishOwnershipValidation {

        @Test
        @DisplayName("Should throw UserNotRestaurantOwnerException when user is not the owner")
        void shouldThrowExceptionWhenUserIsNotOwner() {
            Long differentOwnerId = 999L;
            when(restaurantPersistencePort.findById(RESTAURANT_ID)).thenReturn(Optional.of(restaurant));

            assertThatThrownBy(() -> dishUseCase.createDish(validDish, differentOwnerId))
                    .isInstanceOf(UserNotRestaurantOwnerException.class)
                    .hasMessageContaining(differentOwnerId.toString())
                    .hasMessageContaining(RESTAURANT_ID.toString());

            verify(dishPersistencePort, never()).saveDish(any());
        }
    }

    @Nested
    @DisplayName("Update Dish - Happy Path")
    class UpdateDishHappyPath {

        @Test
        @DisplayName("Should update dish price and description successfully")
        void shouldUpdateDishSuccessfully() {
            Integer newPrice = 30000;
            String newDescription = "Nueva descripción actualizada";

            Dish existingDish = new Dish(
                    "Hamburguesa Clásica",
                    25000,
                    "Descripción original",
                    "https://example.com/burger.jpg",
                    "Hamburguesas",
                    RESTAURANT_ID
            );
            existingDish.setId(DISH_ID);
            existingDish.setActive(true);

            Dish updatedDish = new Dish(
                    "Hamburguesa Clásica",
                    newPrice,
                    newDescription,
                    "https://example.com/burger.jpg",
                    "Hamburguesas",
                    RESTAURANT_ID
            );
            updatedDish.setId(DISH_ID);
            updatedDish.setActive(true);

            when(dishPersistencePort.findById(DISH_ID)).thenReturn(Optional.of(existingDish));
            when(restaurantPersistencePort.findById(RESTAURANT_ID)).thenReturn(Optional.of(restaurant));
            when(dishPersistencePort.saveDish(any(Dish.class))).thenReturn(updatedDish);

            Dish result = dishUseCase.updateDish(DISH_ID, newPrice, newDescription, OWNER_ID);

            assertThat(result).isNotNull();
            assertThat(result.getPrice()).isEqualTo(newPrice);
            assertThat(result.getDescription()).isEqualTo(newDescription);

            verify(dishPersistencePort).findById(DISH_ID);
            verify(restaurantPersistencePort).findById(RESTAURANT_ID);
            verify(dishPersistencePort).saveDish(any(Dish.class));
        }
    }

    @Nested
    @DisplayName("Update Dish - Validations")
    class UpdateDishValidations {

        @Test
        @DisplayName("Should throw DishNotFoundException when dish does not exist")
        void shouldThrowExceptionWhenDishNotFound() {
            when(dishPersistencePort.findById(DISH_ID)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> dishUseCase.updateDish(DISH_ID, 30000, "Nueva descripción", OWNER_ID))
                    .isInstanceOf(DishNotFoundException.class)
                    .hasMessageContaining(DISH_ID.toString());

            verify(dishPersistencePort, never()).saveDish(any());
        }

        @Test
        @DisplayName("Should throw InvalidPriceException when price is invalid")
        void shouldThrowExceptionWhenPriceIsInvalid() {
            assertThatThrownBy(() -> dishUseCase.updateDish(DISH_ID, 0, "Nueva descripción", OWNER_ID))
                    .isInstanceOf(InvalidPriceException.class);

            verify(dishPersistencePort, never()).findById(any());
            verify(dishPersistencePort, never()).saveDish(any());
        }

        @Test
        @DisplayName("Should throw UserNotRestaurantOwnerException when user is not the owner")
        void shouldThrowExceptionWhenUserIsNotOwner() {
            Long differentOwnerId = 999L;

            Dish existingDish = new Dish(
                    "Hamburguesa Clásica",
                    25000,
                    "Descripción original",
                    "https://example.com/burger.jpg",
                    "Hamburguesas",
                    RESTAURANT_ID
            );
            existingDish.setId(DISH_ID);

            when(dishPersistencePort.findById(DISH_ID)).thenReturn(Optional.of(existingDish));
            when(restaurantPersistencePort.findById(RESTAURANT_ID)).thenReturn(Optional.of(restaurant));

            assertThatThrownBy(() -> dishUseCase.updateDish(DISH_ID, 30000, "Nueva descripción", differentOwnerId))
                    .isInstanceOf(UserNotRestaurantOwnerException.class);

            verify(dishPersistencePort, never()).saveDish(any());
        }
    }

    @Nested
    @DisplayName("Change Dish Active Status - Happy Path")
    class ChangeDishActiveStatusHappyPath {

        @Test
        @DisplayName("Should update the active flag successfully")
        void shouldUpdateActiveStatusSuccessfully() {
            Dish existingDish = new Dish(
                    "Hamburguesa Clásica",
                    25000,
                    "Descripción original",
                    "https://example.com/burger.jpg",
                    "Hamburguesas",
                    RESTAURANT_ID
            );
            existingDish.setId(DISH_ID);
            existingDish.setActive(true);

            Dish updatedDish = new Dish(
                    "Hamburguesa Clásica",
                    25000,
                    "Descripción original",
                    "https://example.com/burger.jpg",
                    "Hamburguesas",
                    RESTAURANT_ID
            );
            updatedDish.setId(DISH_ID);
            updatedDish.setActive(false);

            when(dishPersistencePort.findById(DISH_ID)).thenReturn(Optional.of(existingDish));
            when(restaurantPersistencePort.findById(RESTAURANT_ID)).thenReturn(Optional.of(restaurant));
            when(dishPersistencePort.saveDish(any(Dish.class))).thenReturn(updatedDish);

            Dish result = dishUseCase.changeDishActiveStatus(DISH_ID, false, OWNER_ID);

            assertThat(result).isNotNull();
            assertThat(result.getActive()).isFalse();

            verify(dishPersistencePort).findById(DISH_ID);
            verify(restaurantPersistencePort).findById(RESTAURANT_ID);
            verify(dishPersistencePort).saveDish(argThat(dish -> Boolean.FALSE.equals(dish.getActive())));
        }
    }

    @Nested
    @DisplayName("Change Dish Active Status - Validations")
    class ChangeDishActiveStatusValidations {

        @Test
        @DisplayName("Should throw DishNotFoundException when dish does not exist")
        void shouldThrowWhenDishNotFound() {
            when(dishPersistencePort.findById(DISH_ID)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> dishUseCase.changeDishActiveStatus(DISH_ID, false, OWNER_ID))
                    .isInstanceOf(DishNotFoundException.class)
                    .hasMessageContaining(DISH_ID.toString());

            verify(dishPersistencePort, never()).saveDish(any());
        }

        @Test
        @DisplayName("Should throw UserNotRestaurantOwnerException when user is not the owner")
        void shouldThrowWhenUserIsNotOwner() {
            Dish existingDish = new Dish(
                    "Hamburguesa Clásica",
                    25000,
                    "Descripción original",
                    "https://example.com/burger.jpg",
                    "Hamburguesas",
                    RESTAURANT_ID
            );
            existingDish.setId(DISH_ID);

            Long differentOwnerId = 999L;

            when(dishPersistencePort.findById(DISH_ID)).thenReturn(Optional.of(existingDish));
            when(restaurantPersistencePort.findById(RESTAURANT_ID)).thenReturn(Optional.of(restaurant));

            assertThatThrownBy(() -> dishUseCase.changeDishActiveStatus(DISH_ID, true, differentOwnerId))
                    .isInstanceOf(UserNotRestaurantOwnerException.class);

            verify(dishPersistencePort, never()).saveDish(any());
        }

        @Test
        @DisplayName("Should throw InvalidActiveStatusException when active flag is null")
        void shouldThrowWhenActiveIsNull() {
            assertThatThrownBy(() -> dishUseCase.changeDishActiveStatus(DISH_ID, null, OWNER_ID))
                    .isInstanceOf(InvalidActiveStatusException.class)
                    .hasMessageContaining("Active flag must be provided");

            verify(dishPersistencePort, never()).findById(any());
            verify(restaurantPersistencePort, never()).findById(any());
            verify(dishPersistencePort, never()).saveDish(any());
        }
    }
}
