package com.pragma.plazoleta.domain.usecase;

import com.pragma.plazoleta.domain.exception.RestaurantNotFoundException;
import com.pragma.plazoleta.domain.exception.UserNotRestaurantOwnerException;
import com.pragma.plazoleta.domain.model.EmployeeRanking;
import com.pragma.plazoleta.domain.model.OrderEfficiency;
import com.pragma.plazoleta.domain.model.Restaurant;
import com.pragma.plazoleta.domain.spi.IRestaurantPersistencePort;
import com.pragma.plazoleta.domain.spi.ITraceabilityPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EfficiencyUseCaseTest {

    @Mock
    private ITraceabilityPort traceabilityPort;

    @Mock
    private IRestaurantPersistencePort restaurantPersistencePort;

    @InjectMocks
    private EfficiencyUseCase efficiencyUseCase;

    private static final Long RESTAURANT_ID = 1L;
    private static final Long OWNER_ID = 10L;
    private static final Long OTHER_USER_ID = 99L;
    private static final Long ORDER_ID = 100L;
    private static final Long EMPLOYEE_ID = 50L;

    // Helper methods
    private Restaurant createRestaurant(Long id, Long ownerId) {
        Restaurant restaurant = new Restaurant();
        restaurant.setId(id);
        restaurant.setOwnerId(ownerId);
        restaurant.setName("Test Restaurant");
        restaurant.setNit("123456789");
        restaurant.setAddress("Test Address");
        restaurant.setPhone("+573001234567");
        restaurant.setLogoUrl("http://test.com/logo.png");
        return restaurant;
    }

    @Nested
    @DisplayName("Tests for getOrdersEfficiencyByRestaurant")
    class GetOrdersEfficiencyByRestaurantTests {

        private OrderEfficiency createOrderEfficiency(Long orderId, Long restaurantId) {
            OrderEfficiency efficiency = new OrderEfficiency();
            efficiency.setOrderId(orderId);
            efficiency.setRestaurantId(restaurantId);
            efficiency.setEmployeeId(EMPLOYEE_ID);
            efficiency.setEmployeeEmail("employee@test.com");
            efficiency.setStartTime(LocalDateTime.now().minusMinutes(45));
            efficiency.setEndTime(LocalDateTime.now());
            efficiency.setDurationInMinutes(45L);
            efficiency.setFinalStatus("DELIVERED");
            return efficiency;
        }

        @Test
        @DisplayName("Should return orders efficiency when owner is valid")
        void shouldReturnOrdersEfficiencyWhenOwnerIsValid() {
            // Arrange
            Restaurant restaurant = createRestaurant(RESTAURANT_ID, OWNER_ID);
            when(restaurantPersistencePort.findById(RESTAURANT_ID)).thenReturn(Optional.of(restaurant));

            OrderEfficiency efficiency = createOrderEfficiency(ORDER_ID, RESTAURANT_ID);
            when(traceabilityPort.getOrdersEfficiencyByRestaurant(RESTAURANT_ID))
                    .thenReturn(Arrays.asList(efficiency));

            // Act
            List<OrderEfficiency> result = efficiencyUseCase.getOrdersEfficiencyByRestaurant(RESTAURANT_ID, OWNER_ID);

            // Assert
            assertEquals(1, result.size());
            assertEquals(ORDER_ID, result.get(0).getOrderId());
            verify(restaurantPersistencePort, times(1)).findById(RESTAURANT_ID);
            verify(traceabilityPort, times(1)).getOrdersEfficiencyByRestaurant(RESTAURANT_ID);
        }

        @Test
        @DisplayName("Should throw exception when restaurant not found")
        void shouldThrowExceptionWhenRestaurantNotFound() {
            // Arrange
            when(restaurantPersistencePort.findById(RESTAURANT_ID)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(RestaurantNotFoundException.class,
                    () -> efficiencyUseCase.getOrdersEfficiencyByRestaurant(RESTAURANT_ID, OWNER_ID));

            verify(restaurantPersistencePort, times(1)).findById(RESTAURANT_ID);
            verify(traceabilityPort, never()).getOrdersEfficiencyByRestaurant(any());
        }

        @Test
        @DisplayName("Should throw exception when user is not owner")
        void shouldThrowExceptionWhenUserIsNotOwner() {
            // Arrange
            Restaurant restaurant = createRestaurant(RESTAURANT_ID, OWNER_ID);
            when(restaurantPersistencePort.findById(RESTAURANT_ID)).thenReturn(Optional.of(restaurant));

            // Act & Assert
            assertThrows(UserNotRestaurantOwnerException.class,
                    () -> efficiencyUseCase.getOrdersEfficiencyByRestaurant(RESTAURANT_ID, OTHER_USER_ID));

            verify(restaurantPersistencePort, times(1)).findById(RESTAURANT_ID);
            verify(traceabilityPort, never()).getOrdersEfficiencyByRestaurant(any());
        }

        @Test
        @DisplayName("Should return empty list when no orders")
        void shouldReturnEmptyListWhenNoOrders() {
            // Arrange
            Restaurant restaurant = createRestaurant(RESTAURANT_ID, OWNER_ID);
            when(restaurantPersistencePort.findById(RESTAURANT_ID)).thenReturn(Optional.of(restaurant));
            when(traceabilityPort.getOrdersEfficiencyByRestaurant(RESTAURANT_ID))
                    .thenReturn(Collections.emptyList());

            // Act
            List<OrderEfficiency> result = efficiencyUseCase.getOrdersEfficiencyByRestaurant(RESTAURANT_ID, OWNER_ID);

            // Assert
            assertTrue(result.isEmpty());
        }
    }

    @Nested
    @DisplayName("Tests for getEmployeeRankingByRestaurant")
    class GetEmployeeRankingByRestaurantTests {

        private EmployeeRanking createEmployeeRanking(Long employeeId, Long restaurantId) {
            EmployeeRanking ranking = new EmployeeRanking();
            ranking.setEmployeeId(employeeId);
            ranking.setEmployeeEmail("employee@test.com");
            ranking.setRestaurantId(restaurantId);
            ranking.setTotalOrdersCompleted(10L);
            ranking.setAverageDurationInMinutes(30.5);
            ranking.setRankingPosition(1);
            return ranking;
        }

        @Test
        @DisplayName("Should return employee ranking when owner is valid")
        void shouldReturnEmployeeRankingWhenOwnerIsValid() {
            // Arrange
            Restaurant restaurant = createRestaurant(RESTAURANT_ID, OWNER_ID);
            when(restaurantPersistencePort.findById(RESTAURANT_ID)).thenReturn(Optional.of(restaurant));

            EmployeeRanking ranking = createEmployeeRanking(EMPLOYEE_ID, RESTAURANT_ID);
            when(traceabilityPort.getEmployeeRankingByRestaurant(RESTAURANT_ID))
                    .thenReturn(Arrays.asList(ranking));

            // Act
            List<EmployeeRanking> result = efficiencyUseCase.getEmployeeRankingByRestaurant(RESTAURANT_ID, OWNER_ID);

            // Assert
            assertEquals(1, result.size());
            assertEquals(EMPLOYEE_ID, result.get(0).getEmployeeId());
            verify(restaurantPersistencePort, times(1)).findById(RESTAURANT_ID);
            verify(traceabilityPort, times(1)).getEmployeeRankingByRestaurant(RESTAURANT_ID);
        }

        @Test
        @DisplayName("Should throw exception when restaurant not found")
        void shouldThrowExceptionWhenRestaurantNotFound() {
            // Arrange
            when(restaurantPersistencePort.findById(RESTAURANT_ID)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(RestaurantNotFoundException.class,
                    () -> efficiencyUseCase.getEmployeeRankingByRestaurant(RESTAURANT_ID, OWNER_ID));

            verify(restaurantPersistencePort, times(1)).findById(RESTAURANT_ID);
            verify(traceabilityPort, never()).getEmployeeRankingByRestaurant(any());
        }

        @Test
        @DisplayName("Should throw exception when user is not owner")
        void shouldThrowExceptionWhenUserIsNotOwner() {
            // Arrange
            Restaurant restaurant = createRestaurant(RESTAURANT_ID, OWNER_ID);
            when(restaurantPersistencePort.findById(RESTAURANT_ID)).thenReturn(Optional.of(restaurant));

            // Act & Assert
            assertThrows(UserNotRestaurantOwnerException.class,
                    () -> efficiencyUseCase.getEmployeeRankingByRestaurant(RESTAURANT_ID, OTHER_USER_ID));

            verify(restaurantPersistencePort, times(1)).findById(RESTAURANT_ID);
            verify(traceabilityPort, never()).getEmployeeRankingByRestaurant(any());
        }

        @Test
        @DisplayName("Should return empty list when no employees")
        void shouldReturnEmptyListWhenNoEmployees() {
            // Arrange
            Restaurant restaurant = createRestaurant(RESTAURANT_ID, OWNER_ID);
            when(restaurantPersistencePort.findById(RESTAURANT_ID)).thenReturn(Optional.of(restaurant));
            when(traceabilityPort.getEmployeeRankingByRestaurant(RESTAURANT_ID))
                    .thenReturn(Collections.emptyList());

            // Act
            List<EmployeeRanking> result = efficiencyUseCase.getEmployeeRankingByRestaurant(RESTAURANT_ID, OWNER_ID);

            // Assert
            assertTrue(result.isEmpty());
        }
    }
}
