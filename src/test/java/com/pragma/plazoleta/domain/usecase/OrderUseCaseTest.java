package com.pragma.plazoleta.domain.usecase;

import com.pragma.plazoleta.domain.exception.ClientHasActiveOrderException;
import com.pragma.plazoleta.domain.exception.DishNotActiveException;
import com.pragma.plazoleta.domain.exception.DishNotFoundException;
import com.pragma.plazoleta.domain.exception.DishNotFromRestaurantException;
import com.pragma.plazoleta.domain.exception.EmptyOrderException;
import com.pragma.plazoleta.domain.exception.EmployeeNotAssociatedWithRestaurantException;
import com.pragma.plazoleta.domain.exception.InvalidQuantityException;
import com.pragma.plazoleta.domain.exception.RestaurantNotFoundException;
import com.pragma.plazoleta.domain.model.Dish;
import com.pragma.plazoleta.domain.model.Order;
import com.pragma.plazoleta.domain.model.OrderItem;
import com.pragma.plazoleta.domain.model.OrderStatus;
import com.pragma.plazoleta.domain.model.PagedResult;
import com.pragma.plazoleta.domain.model.Restaurant;
import com.pragma.plazoleta.domain.spi.IDishPersistencePort;
import com.pragma.plazoleta.domain.spi.IEmployeeRestaurantPort;
import com.pragma.plazoleta.domain.spi.IOrderPersistencePort;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderUseCaseTest {

    @Mock
    private IOrderPersistencePort orderPersistencePort;

    @Mock
    private IRestaurantPersistencePort restaurantPersistencePort;

    @Mock
    private IDishPersistencePort dishPersistencePort;

    @Mock
    private IEmployeeRestaurantPort employeeRestaurantPort;

    @InjectMocks
    private OrderUseCase orderUseCase;

    private static final Long CLIENT_ID = 1L;
    private static final Long RESTAURANT_ID = 10L;
    private static final Long DISH_ID_1 = 100L;
    private static final Long DISH_ID_2 = 101L;
    private static final Long EMPLOYEE_ID = 50L;

    private Restaurant restaurant;
    private Dish dish1;
    private Dish dish2;

    @BeforeEach
    void setUp() {
        restaurant = new Restaurant(
                "Mi Restaurante",
                "123456789",
                "Calle 123",
                "+573001234567",
                "https://example.com/logo.jpg",
                5L
        );
        restaurant.setId(RESTAURANT_ID);

        dish1 = createActiveDish(DISH_ID_1, "Hamburguesa Clásica", RESTAURANT_ID);
        dish2 = createActiveDish(DISH_ID_2, "Pizza Margherita", RESTAURANT_ID);
    }

    @Nested
    @DisplayName("Create Order - Happy Path")
    class CreateOrderHappyPath {

        @Test
        @DisplayName("Should create order successfully with single dish")
        void shouldCreateOrderWithSingleDish() {
            Order order = createOrder(CLIENT_ID, RESTAURANT_ID, 
                    List.of(new OrderItem(DISH_ID_1, 2)));

            Order savedOrder = createSavedOrder(1L, order);

            when(restaurantPersistencePort.findById(RESTAURANT_ID)).thenReturn(Optional.of(restaurant));
            when(orderPersistencePort.existsActiveOrderByClientId(CLIENT_ID)).thenReturn(false);
            when(dishPersistencePort.findById(DISH_ID_1)).thenReturn(Optional.of(dish1));
            when(orderPersistencePort.saveOrder(any(Order.class))).thenReturn(savedOrder);

            Order result = orderUseCase.createOrder(order);

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getStatus()).isEqualTo(OrderStatus.PENDING);
            assertThat(result.getClientId()).isEqualTo(CLIENT_ID);
            assertThat(result.getRestaurantId()).isEqualTo(RESTAURANT_ID);

            verify(restaurantPersistencePort).findById(RESTAURANT_ID);
            verify(orderPersistencePort).existsActiveOrderByClientId(CLIENT_ID);
            verify(dishPersistencePort).findById(DISH_ID_1);
            verify(orderPersistencePort).saveOrder(any(Order.class));
        }

        @Test
        @DisplayName("Should create order successfully with multiple dishes")
        void shouldCreateOrderWithMultipleDishes() {
            Order order = createOrder(CLIENT_ID, RESTAURANT_ID, Arrays.asList(
                    new OrderItem(DISH_ID_1, 2),
                    new OrderItem(DISH_ID_2, 1)
            ));

            Order savedOrder = createSavedOrder(1L, order);

            when(restaurantPersistencePort.findById(RESTAURANT_ID)).thenReturn(Optional.of(restaurant));
            when(orderPersistencePort.existsActiveOrderByClientId(CLIENT_ID)).thenReturn(false);
            when(dishPersistencePort.findById(DISH_ID_1)).thenReturn(Optional.of(dish1));
            when(dishPersistencePort.findById(DISH_ID_2)).thenReturn(Optional.of(dish2));
            when(orderPersistencePort.saveOrder(any(Order.class))).thenReturn(savedOrder);

            Order result = orderUseCase.createOrder(order);

            assertThat(result).isNotNull();
            assertThat(result.getItems()).hasSize(2);

            verify(dishPersistencePort).findById(DISH_ID_1);
            verify(dishPersistencePort).findById(DISH_ID_2);
        }

        @Test
        @DisplayName("Should set status to PENDING when creating order")
        void shouldSetStatusToPending() {
            Order order = createOrder(CLIENT_ID, RESTAURANT_ID, 
                    List.of(new OrderItem(DISH_ID_1, 1)));
            order.setStatus(null);

            when(restaurantPersistencePort.findById(RESTAURANT_ID)).thenReturn(Optional.of(restaurant));
            when(orderPersistencePort.existsActiveOrderByClientId(CLIENT_ID)).thenReturn(false);
            when(dishPersistencePort.findById(DISH_ID_1)).thenReturn(Optional.of(dish1));
            when(orderPersistencePort.saveOrder(any(Order.class))).thenAnswer(invocation -> {
                Order savedOrder = invocation.getArgument(0);
                assertThat(savedOrder.getStatus()).isEqualTo(OrderStatus.PENDING);
                savedOrder.setId(1L);
                return savedOrder;
            });

            orderUseCase.createOrder(order);

            verify(orderPersistencePort).saveOrder(any(Order.class));
        }

        @Test
        @DisplayName("Should set timestamps when creating order")
        void shouldSetTimestampsWhenCreating() {
            Order order = createOrder(CLIENT_ID, RESTAURANT_ID, 
                    List.of(new OrderItem(DISH_ID_1, 1)));

            when(restaurantPersistencePort.findById(RESTAURANT_ID)).thenReturn(Optional.of(restaurant));
            when(orderPersistencePort.existsActiveOrderByClientId(CLIENT_ID)).thenReturn(false);
            when(dishPersistencePort.findById(DISH_ID_1)).thenReturn(Optional.of(dish1));
            when(orderPersistencePort.saveOrder(any(Order.class))).thenAnswer(invocation -> {
                Order savedOrder = invocation.getArgument(0);
                assertThat(savedOrder.getCreatedAt()).isNotNull();
                assertThat(savedOrder.getUpdatedAt()).isNotNull();
                savedOrder.setId(1L);
                return savedOrder;
            });

            orderUseCase.createOrder(order);

            verify(orderPersistencePort).saveOrder(any(Order.class));
        }
    }

    @Nested
    @DisplayName("Create Order - Empty Order Validation")
    class CreateOrderEmptyValidation {

        @Test
        @DisplayName("Should throw EmptyOrderException when items list is null")
        void shouldThrowWhenItemsListIsNull() {
            Order order = new Order();
            order.setClientId(CLIENT_ID);
            order.setRestaurantId(RESTAURANT_ID);
            order.setItems(null);

            assertThatThrownBy(() -> orderUseCase.createOrder(order))
                    .isInstanceOf(EmptyOrderException.class)
                    .hasMessageContaining("al menos un plato");

            verify(orderPersistencePort, never()).saveOrder(any());
        }

        @Test
        @DisplayName("Should throw EmptyOrderException when items list is empty")
        void shouldThrowWhenItemsListIsEmpty() {
            Order order = createOrder(CLIENT_ID, RESTAURANT_ID, new ArrayList<>());

            assertThatThrownBy(() -> orderUseCase.createOrder(order))
                    .isInstanceOf(EmptyOrderException.class)
                    .hasMessageContaining("al menos un plato");

            verify(orderPersistencePort, never()).saveOrder(any());
        }
    }

    @Nested
    @DisplayName("Create Order - Restaurant Validation")
    class CreateOrderRestaurantValidation {

        @Test
        @DisplayName("Should throw RestaurantNotFoundException when restaurant does not exist")
        void shouldThrowWhenRestaurantNotFound() {
            Order order = createOrder(CLIENT_ID, RESTAURANT_ID, 
                    List.of(new OrderItem(DISH_ID_1, 1)));

            when(restaurantPersistencePort.findById(RESTAURANT_ID)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> orderUseCase.createOrder(order))
                    .isInstanceOf(RestaurantNotFoundException.class)
                    .hasMessageContaining(RESTAURANT_ID.toString());

            verify(orderPersistencePort, never()).saveOrder(any());
        }
    }

    @Nested
    @DisplayName("Create Order - Active Order Validation")
    class CreateOrderActiveOrderValidation {

        @Test
        @DisplayName("Should throw ClientHasActiveOrderException when client has active order")
        void shouldThrowWhenClientHasActiveOrder() {
            Order order = createOrder(CLIENT_ID, RESTAURANT_ID, 
                    List.of(new OrderItem(DISH_ID_1, 1)));

            when(restaurantPersistencePort.findById(RESTAURANT_ID)).thenReturn(Optional.of(restaurant));
            when(orderPersistencePort.existsActiveOrderByClientId(CLIENT_ID)).thenReturn(true);

            assertThatThrownBy(() -> orderUseCase.createOrder(order))
                    .isInstanceOf(ClientHasActiveOrderException.class)
                    .hasMessageContaining(CLIENT_ID.toString())
                    .hasMessageContaining("ya tiene un pedido en proceso");

            verify(orderPersistencePort, never()).saveOrder(any());
        }
    }

    @Nested
    @DisplayName("Create Order - Quantity Validation")
    class CreateOrderQuantityValidation {

        @ParameterizedTest
        @ValueSource(ints = {0, -1, -10, -100})
        @DisplayName("Should throw InvalidQuantityException when quantity is not positive")
        void shouldThrowWhenQuantityIsNotPositive(int invalidQuantity) {
            Order order = createOrder(CLIENT_ID, RESTAURANT_ID, 
                    List.of(new OrderItem(DISH_ID_1, invalidQuantity)));

            when(restaurantPersistencePort.findById(RESTAURANT_ID)).thenReturn(Optional.of(restaurant));
            when(orderPersistencePort.existsActiveOrderByClientId(CLIENT_ID)).thenReturn(false);

            assertThatThrownBy(() -> orderUseCase.createOrder(order))
                    .isInstanceOf(InvalidQuantityException.class)
                    .hasMessageContaining("mayor a 0");

            verify(orderPersistencePort, never()).saveOrder(any());
        }

        @Test
        @DisplayName("Should throw InvalidQuantityException when quantity is null")
        void shouldThrowWhenQuantityIsNull() {
            Order order = createOrder(CLIENT_ID, RESTAURANT_ID, 
                    List.of(new OrderItem(DISH_ID_1, null)));

            when(restaurantPersistencePort.findById(RESTAURANT_ID)).thenReturn(Optional.of(restaurant));
            when(orderPersistencePort.existsActiveOrderByClientId(CLIENT_ID)).thenReturn(false);

            assertThatThrownBy(() -> orderUseCase.createOrder(order))
                    .isInstanceOf(InvalidQuantityException.class);

            verify(orderPersistencePort, never()).saveOrder(any());
        }
    }

    @Nested
    @DisplayName("Create Order - Dish Validation")
    class CreateOrderDishValidation {

        @Test
        @DisplayName("Should throw DishNotFoundException when dish does not exist")
        void shouldThrowWhenDishNotFound() {
            Order order = createOrder(CLIENT_ID, RESTAURANT_ID, 
                    List.of(new OrderItem(DISH_ID_1, 1)));

            when(restaurantPersistencePort.findById(RESTAURANT_ID)).thenReturn(Optional.of(restaurant));
            when(orderPersistencePort.existsActiveOrderByClientId(CLIENT_ID)).thenReturn(false);
            when(dishPersistencePort.findById(DISH_ID_1)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> orderUseCase.createOrder(order))
                    .isInstanceOf(DishNotFoundException.class)
                    .hasMessageContaining(DISH_ID_1.toString());

            verify(orderPersistencePort, never()).saveOrder(any());
        }

        @Test
        @DisplayName("Should throw DishNotFromRestaurantException when dish belongs to different restaurant")
        void shouldThrowWhenDishNotFromRestaurant() {
            Long differentRestaurantId = 999L;
            Dish dishFromDifferentRestaurant = createActiveDish(DISH_ID_1, "Hamburguesa", differentRestaurantId);

            Order order = createOrder(CLIENT_ID, RESTAURANT_ID, 
                    List.of(new OrderItem(DISH_ID_1, 1)));

            when(restaurantPersistencePort.findById(RESTAURANT_ID)).thenReturn(Optional.of(restaurant));
            when(orderPersistencePort.existsActiveOrderByClientId(CLIENT_ID)).thenReturn(false);
            when(dishPersistencePort.findById(DISH_ID_1)).thenReturn(Optional.of(dishFromDifferentRestaurant));

            assertThatThrownBy(() -> orderUseCase.createOrder(order))
                    .isInstanceOf(DishNotFromRestaurantException.class)
                    .hasMessageContaining(DISH_ID_1.toString())
                    .hasMessageContaining(RESTAURANT_ID.toString());

            verify(orderPersistencePort, never()).saveOrder(any());
        }

        @Test
        @DisplayName("Should throw DishNotActiveException when dish is not active")
        void shouldThrowWhenDishNotActive() {
            Dish inactiveDish = createActiveDish(DISH_ID_1, "Hamburguesa", RESTAURANT_ID);
            inactiveDish.setActive(false);

            Order order = createOrder(CLIENT_ID, RESTAURANT_ID, 
                    List.of(new OrderItem(DISH_ID_1, 1)));

            when(restaurantPersistencePort.findById(RESTAURANT_ID)).thenReturn(Optional.of(restaurant));
            when(orderPersistencePort.existsActiveOrderByClientId(CLIENT_ID)).thenReturn(false);
            when(dishPersistencePort.findById(DISH_ID_1)).thenReturn(Optional.of(inactiveDish));

            assertThatThrownBy(() -> orderUseCase.createOrder(order))
                    .isInstanceOf(DishNotActiveException.class)
                    .hasMessageContaining(DISH_ID_1.toString())
                    .hasMessageContaining("no está disponible");

            verify(orderPersistencePort, never()).saveOrder(any());
        }

        @Test
        @DisplayName("Should throw DishNotActiveException when dish active is null")
        void shouldThrowWhenDishActiveIsNull() {
            Dish dishWithNullActive = createActiveDish(DISH_ID_1, "Hamburguesa", RESTAURANT_ID);
            dishWithNullActive.setActive(null);

            Order order = createOrder(CLIENT_ID, RESTAURANT_ID, 
                    List.of(new OrderItem(DISH_ID_1, 1)));

            when(restaurantPersistencePort.findById(RESTAURANT_ID)).thenReturn(Optional.of(restaurant));
            when(orderPersistencePort.existsActiveOrderByClientId(CLIENT_ID)).thenReturn(false);
            when(dishPersistencePort.findById(DISH_ID_1)).thenReturn(Optional.of(dishWithNullActive));

            assertThatThrownBy(() -> orderUseCase.createOrder(order))
                    .isInstanceOf(DishNotActiveException.class);

            verify(orderPersistencePort, never()).saveOrder(any());
        }
    }

    @Nested
    @DisplayName("Create Order - Multiple Dishes Validation")
    class CreateOrderMultipleDishesValidation {

        @Test
        @DisplayName("Should validate all dishes belong to same restaurant")
        void shouldValidateAllDishesBelongToSameRestaurant() {
            Long differentRestaurantId = 999L;
            Dish dishFromDifferentRestaurant = createActiveDish(DISH_ID_2, "Pizza", differentRestaurantId);

            Order order = createOrder(CLIENT_ID, RESTAURANT_ID, Arrays.asList(
                    new OrderItem(DISH_ID_1, 1),
                    new OrderItem(DISH_ID_2, 1)
            ));

            when(restaurantPersistencePort.findById(RESTAURANT_ID)).thenReturn(Optional.of(restaurant));
            when(orderPersistencePort.existsActiveOrderByClientId(CLIENT_ID)).thenReturn(false);
            when(dishPersistencePort.findById(DISH_ID_1)).thenReturn(Optional.of(dish1));
            when(dishPersistencePort.findById(DISH_ID_2)).thenReturn(Optional.of(dishFromDifferentRestaurant));

            assertThatThrownBy(() -> orderUseCase.createOrder(order))
                    .isInstanceOf(DishNotFromRestaurantException.class)
                    .hasMessageContaining(DISH_ID_2.toString());

            verify(orderPersistencePort, never()).saveOrder(any());
        }

        @Test
        @DisplayName("Should fail fast on first invalid quantity")
        void shouldFailFastOnFirstInvalidQuantity() {
            Order order = createOrder(CLIENT_ID, RESTAURANT_ID, Arrays.asList(
                    new OrderItem(DISH_ID_1, 0),
                    new OrderItem(DISH_ID_2, 1)
            ));

            when(restaurantPersistencePort.findById(RESTAURANT_ID)).thenReturn(Optional.of(restaurant));
            when(orderPersistencePort.existsActiveOrderByClientId(CLIENT_ID)).thenReturn(false);

            assertThatThrownBy(() -> orderUseCase.createOrder(order))
                    .isInstanceOf(InvalidQuantityException.class);

            verify(dishPersistencePort, never()).findById(any());
            verify(orderPersistencePort, never()).saveOrder(any());
        }
    }

    @Nested
    @DisplayName("Get Orders By Restaurant And Status")
    class GetOrdersByRestaurantAndStatus {

        @Test
        @DisplayName("Should return paginated orders when employee is associated with restaurant")
        void shouldReturnPaginatedOrdersSuccessfully() {
            int page = 0;
            int size = 10;
            OrderStatus status = OrderStatus.PENDING;

            Order order1 = createSavedOrder(1L, createOrder(CLIENT_ID, RESTAURANT_ID, List.of(new OrderItem(DISH_ID_1, 1))));
            Order order2 = createSavedOrder(2L, createOrder(2L, RESTAURANT_ID, List.of(new OrderItem(DISH_ID_2, 2))));

            PagedResult<Order> expectedResult = PagedResult.of(
                    Arrays.asList(order1, order2),
                    page,
                    size,
                    2L,
                    1
            );

            when(employeeRestaurantPort.getRestaurantIdByEmployeeId(EMPLOYEE_ID))
                    .thenReturn(Optional.of(RESTAURANT_ID));
            when(orderPersistencePort.findByRestaurantIdAndStatusPaginated(eq(RESTAURANT_ID), eq(status), eq(page), eq(size)))
                    .thenReturn(expectedResult);

            PagedResult<Order> result = orderUseCase.getOrdersByRestaurantAndStatus(EMPLOYEE_ID, status, page, size);

            assertThat(result).isNotNull();
            assertThat(result.getContent()).hasSize(2);
            assertThat(result.getPage()).isEqualTo(page);
            assertThat(result.getSize()).isEqualTo(size);
            assertThat(result.getTotalElements()).isEqualTo(2L);

            verify(employeeRestaurantPort).getRestaurantIdByEmployeeId(EMPLOYEE_ID);
            verify(orderPersistencePort).findByRestaurantIdAndStatusPaginated(RESTAURANT_ID, status, page, size);
        }

        @Test
        @DisplayName("Should return empty result when no orders match filter")
        void shouldReturnEmptyResultWhenNoOrdersMatch() {
            int page = 0;
            int size = 10;
            OrderStatus status = OrderStatus.READY;

            PagedResult<Order> emptyResult = PagedResult.of(
                    new ArrayList<>(),
                    page,
                    size,
                    0L,
                    0
            );

            when(employeeRestaurantPort.getRestaurantIdByEmployeeId(EMPLOYEE_ID))
                    .thenReturn(Optional.of(RESTAURANT_ID));
            when(orderPersistencePort.findByRestaurantIdAndStatusPaginated(eq(RESTAURANT_ID), eq(status), eq(page), eq(size)))
                    .thenReturn(emptyResult);

            PagedResult<Order> result = orderUseCase.getOrdersByRestaurantAndStatus(EMPLOYEE_ID, status, page, size);

            assertThat(result).isNotNull();
            assertThat(result.getContent()).isEmpty();
            assertThat(result.getTotalElements()).isZero();
        }

        @Test
        @DisplayName("Should throw exception when employee is not associated with restaurant")
        void shouldThrowWhenEmployeeNotAssociatedWithRestaurant() {
            when(employeeRestaurantPort.getRestaurantIdByEmployeeId(EMPLOYEE_ID))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> orderUseCase.getOrdersByRestaurantAndStatus(EMPLOYEE_ID, OrderStatus.PENDING, 0, 10))
                    .isInstanceOf(EmployeeNotAssociatedWithRestaurantException.class)
                    .hasMessageContaining(EMPLOYEE_ID.toString());

            verify(orderPersistencePort, never()).findByRestaurantIdAndStatusPaginated(any(), any(), anyInt(), anyInt());
        }

        @Test
        @DisplayName("Should handle different order statuses")
        void shouldHandleDifferentOrderStatuses() {
            int page = 0;
            int size = 5;

            when(employeeRestaurantPort.getRestaurantIdByEmployeeId(EMPLOYEE_ID))
                    .thenReturn(Optional.of(RESTAURANT_ID));

            for (OrderStatus status : OrderStatus.values()) {
                PagedResult<Order> mockResult = PagedResult.of(
                        new ArrayList<>(), page, size, 0L, 0);

                when(orderPersistencePort.findByRestaurantIdAndStatusPaginated(RESTAURANT_ID, status, page, size))
                        .thenReturn(mockResult);

                PagedResult<Order> result = orderUseCase.getOrdersByRestaurantAndStatus(EMPLOYEE_ID, status, page, size);

                assertThat(result).isNotNull();
                verify(orderPersistencePort).findByRestaurantIdAndStatusPaginated(RESTAURANT_ID, status, page, size);
            }
        }
    }

    private Order createOrder(Long clientId, Long restaurantId, List<OrderItem> items) {
        Order order = new Order();
        order.setClientId(clientId);
        order.setRestaurantId(restaurantId);
        order.setItems(items);
        return order;
    }

    private Order createSavedOrder(Long orderId, Order originalOrder) {
        Order savedOrder = new Order();
        savedOrder.setId(orderId);
        savedOrder.setClientId(originalOrder.getClientId());
        savedOrder.setRestaurantId(originalOrder.getRestaurantId());
        savedOrder.setItems(originalOrder.getItems());
        savedOrder.setStatus(OrderStatus.PENDING);
        savedOrder.setCreatedAt(LocalDateTime.now());
        savedOrder.setUpdatedAt(LocalDateTime.now());
        return savedOrder;
    }

    private Dish createActiveDish(Long id, String name, Long restaurantId) {
        Dish dish = new Dish(
                name,
                25000,
                "Descripción del plato",
                "https://example.com/image.jpg",
                "Categoría",
                restaurantId
        );
        dish.setId(id);
        dish.setActive(true);
        return dish;
    }
}
