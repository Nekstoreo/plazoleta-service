package com.pragma.plazoleta.domain.usecase;

import com.pragma.plazoleta.domain.exception.ClientHasActiveOrderException;
import com.pragma.plazoleta.domain.exception.ClientPhoneNotFoundException;
import com.pragma.plazoleta.domain.exception.DishNotActiveException;
import com.pragma.plazoleta.domain.exception.DishNotFoundException;
import com.pragma.plazoleta.domain.exception.DishNotFromRestaurantException;
import com.pragma.plazoleta.domain.exception.EmptyOrderException;
import com.pragma.plazoleta.domain.exception.EmployeeNotAssociatedWithRestaurantException;
import com.pragma.plazoleta.domain.exception.InvalidQuantityException;
import com.pragma.plazoleta.domain.exception.InvalidSecurityPinException;
import com.pragma.plazoleta.domain.exception.OrderNotInPreparationException;
import com.pragma.plazoleta.domain.exception.RestaurantNotFoundException;
import com.pragma.plazoleta.domain.model.Dish;
import com.pragma.plazoleta.domain.model.Order;
import com.pragma.plazoleta.domain.model.OrderItem;
import com.pragma.plazoleta.domain.model.OrderStatus;
import com.pragma.plazoleta.domain.model.PagedResult;
import com.pragma.plazoleta.domain.model.Restaurant;
import com.pragma.plazoleta.domain.spi.IClientInfoPort;
import com.pragma.plazoleta.domain.spi.IDishPersistencePort;
import com.pragma.plazoleta.domain.spi.IEmployeeRestaurantPort;
import com.pragma.plazoleta.domain.spi.INotificationPort;
import com.pragma.plazoleta.domain.spi.IOrderPersistencePort;
import com.pragma.plazoleta.domain.spi.IRestaurantPersistencePort;
import com.pragma.plazoleta.domain.spi.ITraceabilityPort;
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
import static org.mockito.ArgumentMatchers.anyString;
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

    @Mock
    private IClientInfoPort clientInfoPort;

    @Mock
    private INotificationPort notificationPort;

    @Mock
    private ITraceabilityPort traceabilityPort;

    @InjectMocks
    private OrderUseCase orderUseCase;

    private static final Long CLIENT_ID = 1L;
    private static final Long RESTAURANT_ID = 10L;
    private static final Long DISH_ID_1 = 100L;
    private static final Long DISH_ID_2 = 101L;
    private static final Long EMPLOYEE_ID = 50L;
    private static final String CLIENT_PHONE = "+573001234567";
    private static final String DISH_NAME_HAMBURGUESA = "Hamburguesa";

    private Restaurant restaurant;
    private Dish dish1;
    private Dish dish2;

    @BeforeEach
    void setUp() {
        restaurant = new Restaurant(
                "Mi Restaurante",
                "123456789",
                "Calle 123",
                CLIENT_PHONE,
                "https://example.com/logo.jpg",
                5L
        );
        dish1 = createActiveDish(DISH_ID_1, "Hamburguesa Clásica", RESTAURANT_ID);
        dish2 = createActiveDish(DISH_ID_2, "Pizza Margherita", RESTAURANT_ID);

        // Default mocks to avoid NPEs in traceability logic
        org.mockito.Mockito.lenient().when(clientInfoPort.getClientEmailById(any())).thenReturn(Optional.of("client@test.com"));
        org.mockito.Mockito.lenient().when(employeeRestaurantPort.getEmployeeEmailById(any())).thenReturn(Optional.of("employee@test.com"));
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
                    .hasMessageContaining("at least one dish");

            verify(orderPersistencePort, never()).saveOrder(any());
        }

        @Test
        @DisplayName("Should throw EmptyOrderException when items list is empty")
        void shouldThrowWhenItemsListIsEmpty() {
            Order order = createOrder(CLIENT_ID, RESTAURANT_ID, new ArrayList<>());

            assertThatThrownBy(() -> orderUseCase.createOrder(order))
                    .isInstanceOf(EmptyOrderException.class)
                    .hasMessageContaining("at least one dish");

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
                    .hasMessageContaining("already has an active order");

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
                    .hasMessageContaining("greater than 0");

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
            Dish dishFromDifferentRestaurant = createActiveDish(DISH_ID_1, DISH_NAME_HAMBURGUESA, differentRestaurantId);

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
            Dish inactiveDish = createActiveDish(DISH_ID_1, DISH_NAME_HAMBURGUESA, RESTAURANT_ID);
            inactiveDish.setActive(false);

            Order order = createOrder(CLIENT_ID, RESTAURANT_ID, 
                    List.of(new OrderItem(DISH_ID_1, 1)));

            when(restaurantPersistencePort.findById(RESTAURANT_ID)).thenReturn(Optional.of(restaurant));
            when(orderPersistencePort.existsActiveOrderByClientId(CLIENT_ID)).thenReturn(false);
            when(dishPersistencePort.findById(DISH_ID_1)).thenReturn(Optional.of(inactiveDish));

            assertThatThrownBy(() -> orderUseCase.createOrder(order))
                    .isInstanceOf(DishNotActiveException.class)
                    .hasMessageContaining(DISH_ID_1.toString())
                    .hasMessageContaining("not currently available");

            verify(orderPersistencePort, never()).saveOrder(any());
        }

        @Test
        @DisplayName("Should throw DishNotActiveException when dish active is null")
        void shouldThrowWhenDishActiveIsNull() {
            Dish dishWithNullActive = createActiveDish(DISH_ID_1, DISH_NAME_HAMBURGUESA, RESTAURANT_ID);
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
            when(orderPersistencePort.findByRestaurantIdAndStatusPaginated(RESTAURANT_ID, status, page, size))
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
            when(orderPersistencePort.findByRestaurantIdAndStatusPaginated(RESTAURANT_ID, status, page, size))
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
                "Dish description",
                "https://example.com/image.jpg",
                "Category",
                restaurantId
        );
        dish.setId(id);
        dish.setActive(true);
        return dish;
    }

    @Nested
    @DisplayName("Mark Order As Delivered")
    class MarkOrderAsDeliveredTests {

        private static final Long ORDER_ID = 200L;
        private static final String VALID_SECURITY_PIN = "123456";

        @Test
        @DisplayName("Should mark order as delivered successfully when order is READY")
        void shouldMarkOrderAsDeliveredSuccessfully() {
            Order readyOrder = createOrderWithStatus(ORDER_ID, RESTAURANT_ID, OrderStatus.READY);
            readyOrder.setEmployeeId(EMPLOYEE_ID);
            readyOrder.setSecurityPin(VALID_SECURITY_PIN);

            Order expectedDeliveredOrder = createOrderWithStatus(ORDER_ID, RESTAURANT_ID, OrderStatus.DELIVERED);
            expectedDeliveredOrder.setEmployeeId(EMPLOYEE_ID);
            expectedDeliveredOrder.setSecurityPin(VALID_SECURITY_PIN);

            when(employeeRestaurantPort.getRestaurantIdByEmployeeId(EMPLOYEE_ID))
                    .thenReturn(Optional.of(RESTAURANT_ID));
            when(orderPersistencePort.findById(ORDER_ID))
                    .thenReturn(Optional.of(readyOrder));
            when(orderPersistencePort.saveOrder(any(Order.class)))
                    .thenReturn(expectedDeliveredOrder);

            Order result = orderUseCase.markOrderAsDelivered(ORDER_ID, EMPLOYEE_ID, VALID_SECURITY_PIN);

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(ORDER_ID);
            assertThat(result.getStatus()).isEqualTo(OrderStatus.DELIVERED);
            assertThat(result.getEmployeeId()).isEqualTo(EMPLOYEE_ID);

            verify(employeeRestaurantPort).getRestaurantIdByEmployeeId(EMPLOYEE_ID);
            verify(orderPersistencePort).findById(ORDER_ID);
            verify(orderPersistencePort).saveOrder(any(Order.class));
        }

        @Test
        @DisplayName("Should throw OrderNotFoundException when order does not exist")
        void shouldThrowOrderNotFoundExceptionWhenOrderDoesNotExist() {
            when(employeeRestaurantPort.getRestaurantIdByEmployeeId(EMPLOYEE_ID))
                    .thenReturn(Optional.of(RESTAURANT_ID));
            when(orderPersistencePort.findById(ORDER_ID))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> orderUseCase.markOrderAsDelivered(ORDER_ID, EMPLOYEE_ID, VALID_SECURITY_PIN))
                    .isInstanceOf(com.pragma.plazoleta.domain.exception.OrderNotFoundException.class)
                    .hasMessageContaining(ORDER_ID.toString());

            verify(orderPersistencePort, never()).saveOrder(any());
        }

        @Test
        @DisplayName("Should throw InvalidOrderStatusException when order is PENDING")
        void shouldThrowInvalidOrderStatusExceptionWhenOrderIsPending() {
            Order pendingOrder = createOrderWithStatus(ORDER_ID, RESTAURANT_ID, OrderStatus.PENDING);

            when(employeeRestaurantPort.getRestaurantIdByEmployeeId(EMPLOYEE_ID))
                    .thenReturn(Optional.of(RESTAURANT_ID));
            when(orderPersistencePort.findById(ORDER_ID))
                    .thenReturn(Optional.of(pendingOrder));

            assertThatThrownBy(() -> orderUseCase.markOrderAsDelivered(ORDER_ID, EMPLOYEE_ID, VALID_SECURITY_PIN))
                    .isInstanceOf(com.pragma.plazoleta.domain.exception.InvalidOrderStatusException.class)
                    .hasMessageContaining("READY");

            verify(orderPersistencePort, never()).saveOrder(any());
        }

        @Test
        @DisplayName("Should throw InvalidOrderStatusException when order is IN_PREPARATION")
        void shouldThrowInvalidOrderStatusExceptionWhenOrderIsInPreparation() {
            Order inPreparationOrder = createOrderWithStatus(ORDER_ID, RESTAURANT_ID, OrderStatus.IN_PREPARATION);

            when(employeeRestaurantPort.getRestaurantIdByEmployeeId(EMPLOYEE_ID))
                    .thenReturn(Optional.of(RESTAURANT_ID));
            when(orderPersistencePort.findById(ORDER_ID))
                    .thenReturn(Optional.of(inPreparationOrder));

            assertThatThrownBy(() -> orderUseCase.markOrderAsDelivered(ORDER_ID, EMPLOYEE_ID, VALID_SECURITY_PIN))
                    .isInstanceOf(com.pragma.plazoleta.domain.exception.InvalidOrderStatusException.class);

            verify(orderPersistencePort, never()).saveOrder(any());
        }

        @Test
        @DisplayName("Should throw InvalidOrderStatusException when order is CANCELLED")
        void shouldThrowInvalidOrderStatusExceptionWhenOrderIsCancelled() {
            Order cancelledOrder = createOrderWithStatus(ORDER_ID, RESTAURANT_ID, OrderStatus.CANCELLED);

            when(employeeRestaurantPort.getRestaurantIdByEmployeeId(EMPLOYEE_ID))
                    .thenReturn(Optional.of(RESTAURANT_ID));
            when(orderPersistencePort.findById(ORDER_ID))
                    .thenReturn(Optional.of(cancelledOrder));

            assertThatThrownBy(() -> orderUseCase.markOrderAsDelivered(ORDER_ID, EMPLOYEE_ID, VALID_SECURITY_PIN))
                    .isInstanceOf(com.pragma.plazoleta.domain.exception.InvalidOrderStatusException.class);

            verify(orderPersistencePort, never()).saveOrder(any());
        }

        @Test
        @DisplayName("Should throw InvalidOrderStatusException when order is already DELIVERED")
        void shouldThrowInvalidOrderStatusExceptionWhenOrderIsAlreadyDelivered() {
            Order deliveredOrder = createOrderWithStatus(ORDER_ID, RESTAURANT_ID, OrderStatus.DELIVERED);

            when(employeeRestaurantPort.getRestaurantIdByEmployeeId(EMPLOYEE_ID))
                    .thenReturn(Optional.of(RESTAURANT_ID));
            when(orderPersistencePort.findById(ORDER_ID))
                    .thenReturn(Optional.of(deliveredOrder));

            assertThatThrownBy(() -> orderUseCase.markOrderAsDelivered(ORDER_ID, EMPLOYEE_ID, VALID_SECURITY_PIN))
                    .isInstanceOf(com.pragma.plazoleta.domain.exception.InvalidOrderStatusException.class);

            verify(orderPersistencePort, never()).saveOrder(any());
        }

        @Test
        @DisplayName("Should throw InvalidSecurityPinException when provided PIN is incorrect")
        void shouldThrowInvalidSecurityPinExceptionWhenPinIsIncorrect() {
            Order readyOrder = createOrderWithStatus(ORDER_ID, RESTAURANT_ID, OrderStatus.READY);
            readyOrder.setEmployeeId(EMPLOYEE_ID);
            readyOrder.setSecurityPin(VALID_SECURITY_PIN);

            String incorrectPin = "999999";

            when(employeeRestaurantPort.getRestaurantIdByEmployeeId(EMPLOYEE_ID))
                    .thenReturn(Optional.of(RESTAURANT_ID));
            when(orderPersistencePort.findById(ORDER_ID))
                    .thenReturn(Optional.of(readyOrder));

            assertThatThrownBy(() -> orderUseCase.markOrderAsDelivered(ORDER_ID, EMPLOYEE_ID, incorrectPin))
                    .isInstanceOf(InvalidSecurityPinException.class)
                    .hasMessageContaining("security PIN");

            verify(orderPersistencePort, never()).saveOrder(any());
        }

        @Test
        @DisplayName("Should throw InvalidSecurityPinException when provided PIN is null")
        void shouldThrowInvalidSecurityPinExceptionWhenPinIsNull() {
            Order readyOrder = createOrderWithStatus(ORDER_ID, RESTAURANT_ID, OrderStatus.READY);
            readyOrder.setEmployeeId(EMPLOYEE_ID);
            readyOrder.setSecurityPin(VALID_SECURITY_PIN);

            when(employeeRestaurantPort.getRestaurantIdByEmployeeId(EMPLOYEE_ID))
                    .thenReturn(Optional.of(RESTAURANT_ID));
            when(orderPersistencePort.findById(ORDER_ID))
                    .thenReturn(Optional.of(readyOrder));

            assertThatThrownBy(() -> orderUseCase.markOrderAsDelivered(ORDER_ID, EMPLOYEE_ID, null))
                    .isInstanceOf(InvalidSecurityPinException.class);

            verify(orderPersistencePort, never()).saveOrder(any());
        }

        @Test
        @DisplayName("Should throw InvalidSecurityPinException when order PIN is null")
        void shouldThrowInvalidSecurityPinExceptionWhenOrderPinIsNull() {
            Order readyOrder = createOrderWithStatus(ORDER_ID, RESTAURANT_ID, OrderStatus.READY);
            readyOrder.setEmployeeId(EMPLOYEE_ID);
            readyOrder.setSecurityPin(null);

            when(employeeRestaurantPort.getRestaurantIdByEmployeeId(EMPLOYEE_ID))
                    .thenReturn(Optional.of(RESTAURANT_ID));
            when(orderPersistencePort.findById(ORDER_ID))
                    .thenReturn(Optional.of(readyOrder));

            assertThatThrownBy(() -> orderUseCase.markOrderAsDelivered(ORDER_ID, EMPLOYEE_ID, VALID_SECURITY_PIN))
                    .isInstanceOf(InvalidSecurityPinException.class);

            verify(orderPersistencePort, never()).saveOrder(any());
        }

        @Test
        @DisplayName("Should throw OrderNotFromEmployeeRestaurantException when order does not belong to employee's restaurant")
        void shouldThrowOrderNotFromEmployeeRestaurantException() {
            Long differentRestaurantId = 999L;
            Order orderFromDifferentRestaurant = createOrderWithStatus(ORDER_ID, differentRestaurantId, OrderStatus.READY);
            orderFromDifferentRestaurant.setSecurityPin(VALID_SECURITY_PIN);

            when(employeeRestaurantPort.getRestaurantIdByEmployeeId(EMPLOYEE_ID))
                    .thenReturn(Optional.of(RESTAURANT_ID));
            when(orderPersistencePort.findById(ORDER_ID))
                    .thenReturn(Optional.of(orderFromDifferentRestaurant));

            assertThatThrownBy(() -> orderUseCase.markOrderAsDelivered(ORDER_ID, EMPLOYEE_ID, VALID_SECURITY_PIN))
                    .isInstanceOf(com.pragma.plazoleta.domain.exception.OrderNotFromEmployeeRestaurantException.class)
                    .hasMessageContaining(ORDER_ID.toString());

            verify(orderPersistencePort, never()).saveOrder(any());
        }

        @Test
        @DisplayName("Should throw EmployeeNotAssociatedWithRestaurantException when employee has no restaurant")
        void shouldThrowEmployeeNotAssociatedWithRestaurantException() {
            when(employeeRestaurantPort.getRestaurantIdByEmployeeId(EMPLOYEE_ID))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> orderUseCase.markOrderAsDelivered(ORDER_ID, EMPLOYEE_ID, VALID_SECURITY_PIN))
                    .isInstanceOf(EmployeeNotAssociatedWithRestaurantException.class)
                    .hasMessageContaining(EMPLOYEE_ID.toString());

            verify(orderPersistencePort, never()).findById(any());
            verify(orderPersistencePort, never()).saveOrder(any());
        }

        @Test
        @DisplayName("Should update order timestamp when marking as delivered")
        void shouldUpdateOrderTimestamp() {
            Order readyOrder = createOrderWithStatus(ORDER_ID, RESTAURANT_ID, OrderStatus.READY);
            readyOrder.setEmployeeId(EMPLOYEE_ID);
            readyOrder.setSecurityPin(VALID_SECURITY_PIN);

            when(employeeRestaurantPort.getRestaurantIdByEmployeeId(EMPLOYEE_ID))
                    .thenReturn(Optional.of(RESTAURANT_ID));
            when(orderPersistencePort.findById(ORDER_ID))
                    .thenReturn(Optional.of(readyOrder));
            when(orderPersistencePort.saveOrder(any(Order.class)))
                    .thenAnswer(invocation -> {
                        Order savedOrder = invocation.getArgument(0);
                        assertThat(savedOrder.getUpdatedAt()).isNotNull();
                        return savedOrder;
                    });

            orderUseCase.markOrderAsDelivered(ORDER_ID, EMPLOYEE_ID, VALID_SECURITY_PIN);

            verify(orderPersistencePort).saveOrder(any(Order.class));
        }

        @Test
        @DisplayName("Should persist order with DELIVERED status")
        void shouldPersistOrderWithDeliveredStatus() {
            Order readyOrder = createOrderWithStatus(ORDER_ID, RESTAURANT_ID, OrderStatus.READY);
            readyOrder.setEmployeeId(EMPLOYEE_ID);
            readyOrder.setSecurityPin(VALID_SECURITY_PIN);

            when(employeeRestaurantPort.getRestaurantIdByEmployeeId(EMPLOYEE_ID))
                    .thenReturn(Optional.of(RESTAURANT_ID));
            when(orderPersistencePort.findById(ORDER_ID))
                    .thenReturn(Optional.of(readyOrder));
            when(orderPersistencePort.saveOrder(any(Order.class)))
                    .thenAnswer(invocation -> {
                        Order orderToSave = invocation.getArgument(0);
                        assertThat(orderToSave.getStatus()).isEqualTo(OrderStatus.DELIVERED);
                        orderToSave.setId(ORDER_ID);
                        return orderToSave;
                    });

            Order result = orderUseCase.markOrderAsDelivered(ORDER_ID, EMPLOYEE_ID, VALID_SECURITY_PIN);

            assertThat(result.getStatus()).isEqualTo(OrderStatus.DELIVERED);
            verify(orderPersistencePort).saveOrder(any(Order.class));
        }

        @Test
        @DisplayName("Should correctly validate security PIN with valid PIN")
        void shouldCorrectlyValidateSecurityPinWithValidPin() {
            String actualPin = "654321";
            Order readyOrder = createOrderWithStatus(ORDER_ID, RESTAURANT_ID, OrderStatus.READY);
            readyOrder.setEmployeeId(EMPLOYEE_ID);
            readyOrder.setSecurityPin(actualPin);

            when(employeeRestaurantPort.getRestaurantIdByEmployeeId(EMPLOYEE_ID))
                    .thenReturn(Optional.of(RESTAURANT_ID));
            when(orderPersistencePort.findById(ORDER_ID))
                    .thenReturn(Optional.of(readyOrder));
            when(orderPersistencePort.saveOrder(any(Order.class)))
                    .thenReturn(readyOrder);

            Order result = orderUseCase.markOrderAsDelivered(ORDER_ID, EMPLOYEE_ID, actualPin);

            assertThat(result).isNotNull();
            verify(orderPersistencePort).saveOrder(any(Order.class));
        }
    }

    @Nested
    @DisplayName("Assign Order To Employee")
    class AssignOrderToEmployeeTests {

        private static final Long ORDER_ID = 200L;

        @Test
        @DisplayName("Should assign order to employee successfully when order is PENDING")
        void shouldAssignOrderSuccessfullyWhenPending() {
            Order pendingOrder = createOrderWithStatus(ORDER_ID, RESTAURANT_ID, OrderStatus.PENDING);
            Order expectedAssignedOrder = createOrderWithStatus(ORDER_ID, RESTAURANT_ID, OrderStatus.IN_PREPARATION);
            expectedAssignedOrder.setEmployeeId(EMPLOYEE_ID);

            when(employeeRestaurantPort.getRestaurantIdByEmployeeId(EMPLOYEE_ID))
                    .thenReturn(Optional.of(RESTAURANT_ID));
            when(orderPersistencePort.findById(ORDER_ID))
                    .thenReturn(Optional.of(pendingOrder));
            when(orderPersistencePort.saveOrder(any(Order.class)))
                    .thenReturn(expectedAssignedOrder);

            Order result = orderUseCase.assignOrderToEmployee(ORDER_ID, EMPLOYEE_ID);

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(ORDER_ID);
            assertThat(result.getEmployeeId()).isEqualTo(EMPLOYEE_ID);
            assertThat(result.getStatus()).isEqualTo(OrderStatus.IN_PREPARATION);

            verify(employeeRestaurantPort).getRestaurantIdByEmployeeId(EMPLOYEE_ID);
            verify(orderPersistencePort).findById(ORDER_ID);
            verify(orderPersistencePort).saveOrder(any(Order.class));
        }

        @Test
        @DisplayName("Should throw OrderNotFoundException when order does not exist")
        void shouldThrowOrderNotFoundExceptionWhenOrderDoesNotExist() {
            when(employeeRestaurantPort.getRestaurantIdByEmployeeId(EMPLOYEE_ID))
                    .thenReturn(Optional.of(RESTAURANT_ID));
            when(orderPersistencePort.findById(ORDER_ID))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> orderUseCase.assignOrderToEmployee(ORDER_ID, EMPLOYEE_ID))
                    .isInstanceOf(com.pragma.plazoleta.domain.exception.OrderNotFoundException.class)
                    .hasMessageContaining(ORDER_ID.toString());

            verify(orderPersistencePort, never()).saveOrder(any());
        }

        @Test
        @DisplayName("Should throw InvalidOrderStatusException when order is not PENDING")
        void shouldThrowInvalidOrderStatusExceptionWhenOrderIsNotPending() {
            Order inPreparationOrder = createOrderWithStatus(ORDER_ID, RESTAURANT_ID, OrderStatus.IN_PREPARATION);

            when(employeeRestaurantPort.getRestaurantIdByEmployeeId(EMPLOYEE_ID))
                    .thenReturn(Optional.of(RESTAURANT_ID));
            when(orderPersistencePort.findById(ORDER_ID))
                    .thenReturn(Optional.of(inPreparationOrder));

            assertThatThrownBy(() -> orderUseCase.assignOrderToEmployee(ORDER_ID, EMPLOYEE_ID))
                    .isInstanceOf(com.pragma.plazoleta.domain.exception.InvalidOrderStatusException.class)
                    .hasMessageContaining("PENDING")
                    .hasMessageContaining("IN_PREPARATION");

            verify(orderPersistencePort, never()).saveOrder(any());
        }

        @Test
        @DisplayName("Should throw OrderNotFromEmployeeRestaurantException when order does not belong to employee's restaurant")
        void shouldThrowOrderNotFromEmployeeRestaurantException() {
            Long differentRestaurantId = 999L;
            Order orderFromDifferentRestaurant = createOrderWithStatus(ORDER_ID, differentRestaurantId, OrderStatus.PENDING);

            when(employeeRestaurantPort.getRestaurantIdByEmployeeId(EMPLOYEE_ID))
                    .thenReturn(Optional.of(RESTAURANT_ID));
            when(orderPersistencePort.findById(ORDER_ID))
                    .thenReturn(Optional.of(orderFromDifferentRestaurant));

            assertThatThrownBy(() -> orderUseCase.assignOrderToEmployee(ORDER_ID, EMPLOYEE_ID))
                    .isInstanceOf(com.pragma.plazoleta.domain.exception.OrderNotFromEmployeeRestaurantException.class)
                    .hasMessageContaining(ORDER_ID.toString());

            verify(orderPersistencePort, never()).saveOrder(any());
        }

        @Test
        @DisplayName("Should throw EmployeeNotAssociatedWithRestaurantException when employee has no restaurant")
        void shouldThrowEmployeeNotAssociatedWithRestaurantException() {
            when(employeeRestaurantPort.getRestaurantIdByEmployeeId(EMPLOYEE_ID))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> orderUseCase.assignOrderToEmployee(ORDER_ID, EMPLOYEE_ID))
                    .isInstanceOf(EmployeeNotAssociatedWithRestaurantException.class)
                    .hasMessageContaining(EMPLOYEE_ID.toString());

            verify(orderPersistencePort, never()).findById(any());
            verify(orderPersistencePort, never()).saveOrder(any());
        }

        @Test
        @DisplayName("Should throw InvalidOrderStatusException for READY status")
        void shouldThrowInvalidOrderStatusExceptionForReadyStatus() {
            Order readyOrder = createOrderWithStatus(ORDER_ID, RESTAURANT_ID, OrderStatus.READY);

            when(employeeRestaurantPort.getRestaurantIdByEmployeeId(EMPLOYEE_ID))
                    .thenReturn(Optional.of(RESTAURANT_ID));
            when(orderPersistencePort.findById(ORDER_ID))
                    .thenReturn(Optional.of(readyOrder));

            assertThatThrownBy(() -> orderUseCase.assignOrderToEmployee(ORDER_ID, EMPLOYEE_ID))
                    .isInstanceOf(com.pragma.plazoleta.domain.exception.InvalidOrderStatusException.class);

            verify(orderPersistencePort, never()).saveOrder(any());
        }

        @Test
        @DisplayName("Should throw InvalidOrderStatusException for DELIVERED status")
        void shouldThrowInvalidOrderStatusExceptionForDeliveredStatus() {
            Order deliveredOrder = createOrderWithStatus(ORDER_ID, RESTAURANT_ID, OrderStatus.DELIVERED);

            when(employeeRestaurantPort.getRestaurantIdByEmployeeId(EMPLOYEE_ID))
                    .thenReturn(Optional.of(RESTAURANT_ID));
            when(orderPersistencePort.findById(ORDER_ID))
                    .thenReturn(Optional.of(deliveredOrder));

            assertThatThrownBy(() -> orderUseCase.assignOrderToEmployee(ORDER_ID, EMPLOYEE_ID))
                    .isInstanceOf(com.pragma.plazoleta.domain.exception.InvalidOrderStatusException.class);

            verify(orderPersistencePort, never()).saveOrder(any());
        }

        @Test
        @DisplayName("Should throw InvalidOrderStatusException for CANCELLED status")
        void shouldThrowInvalidOrderStatusExceptionForCancelledStatus() {
            Order cancelledOrder = createOrderWithStatus(ORDER_ID, RESTAURANT_ID, OrderStatus.CANCELLED);

            when(employeeRestaurantPort.getRestaurantIdByEmployeeId(EMPLOYEE_ID))
                    .thenReturn(Optional.of(RESTAURANT_ID));
            when(orderPersistencePort.findById(ORDER_ID))
                    .thenReturn(Optional.of(cancelledOrder));

            assertThatThrownBy(() -> orderUseCase.assignOrderToEmployee(ORDER_ID, EMPLOYEE_ID))
                    .isInstanceOf(com.pragma.plazoleta.domain.exception.InvalidOrderStatusException.class);

            verify(orderPersistencePort, never()).saveOrder(any());
        }
    }

    private Order createOrderWithStatus(Long orderId, Long restaurantId, OrderStatus status) {
        Order order = new Order();
        order.setId(orderId);
        order.setClientId(OrderUseCaseTest.CLIENT_ID);
        order.setRestaurantId(restaurantId);
        order.setStatus(status);
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        order.setItems(new ArrayList<>());
        return order;
    }

    @Nested
    @DisplayName("Mark Order As Ready")
    class MarkOrderAsReadyTests {

        private static final Long ORDER_ID = 200L;
        private static final String CLIENT_PHONE = "+573001234567";

        @Test
        @DisplayName("Should mark order as ready successfully when order is IN_PREPARATION")
        void shouldMarkOrderAsReadySuccessfully() {
            Order inPreparationOrder = createOrderWithStatus(ORDER_ID, RESTAURANT_ID, OrderStatus.IN_PREPARATION);
            inPreparationOrder.setEmployeeId(EMPLOYEE_ID);

            when(employeeRestaurantPort.getRestaurantIdByEmployeeId(EMPLOYEE_ID))
                    .thenReturn(Optional.of(RESTAURANT_ID));
            when(orderPersistencePort.findById(ORDER_ID))
                    .thenReturn(Optional.of(inPreparationOrder));
            when(orderPersistencePort.saveOrder(any(Order.class)))
                    .thenAnswer(invocation -> {
                        Order savedOrder = invocation.getArgument(0);
                        assertThat(savedOrder.getStatus()).isEqualTo(OrderStatus.READY);
                        assertThat(savedOrder.getSecurityPin()).isNotNull();
                        assertThat(savedOrder.getSecurityPin()).hasSize(6);
                        return savedOrder;
                    });
            when(clientInfoPort.getClientPhoneById(CLIENT_ID))
                    .thenReturn(Optional.of(CLIENT_PHONE));
            when(restaurantPersistencePort.findById(RESTAURANT_ID))
                    .thenReturn(Optional.of(restaurant));

            Order result = orderUseCase.markOrderAsReady(ORDER_ID, EMPLOYEE_ID);

            assertThat(result).isNotNull();
            assertThat(result.getStatus()).isEqualTo(OrderStatus.READY);
            assertThat(result.getSecurityPin()).isNotNull();
            assertThat(result.getSecurityPin()).hasSize(6);

            verify(employeeRestaurantPort).getRestaurantIdByEmployeeId(EMPLOYEE_ID);
            verify(orderPersistencePort).findById(ORDER_ID);
            verify(orderPersistencePort).saveOrder(any(Order.class));
            verify(clientInfoPort).getClientPhoneById(CLIENT_ID);
            verify(notificationPort).sendOrderReadyNotification(
                    eq(CLIENT_PHONE),
                    eq(ORDER_ID.toString()),
                    anyString(),
                    eq(restaurant.getName())
            );
        }

        @Test
        @DisplayName("Should generate 6 digit security PIN")
        void shouldGenerateSixDigitSecurityPin() {
            Order inPreparationOrder = createOrderWithStatus(ORDER_ID, RESTAURANT_ID, OrderStatus.IN_PREPARATION);
            inPreparationOrder.setEmployeeId(EMPLOYEE_ID);

            when(employeeRestaurantPort.getRestaurantIdByEmployeeId(EMPLOYEE_ID))
                    .thenReturn(Optional.of(RESTAURANT_ID));
            when(orderPersistencePort.findById(ORDER_ID))
                    .thenReturn(Optional.of(inPreparationOrder));
            when(orderPersistencePort.saveOrder(any(Order.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));
            when(clientInfoPort.getClientPhoneById(CLIENT_ID))
                    .thenReturn(Optional.of(CLIENT_PHONE));
            when(restaurantPersistencePort.findById(RESTAURANT_ID))
                    .thenReturn(Optional.of(restaurant));

            Order result = orderUseCase.markOrderAsReady(ORDER_ID, EMPLOYEE_ID);

            assertThat(result.getSecurityPin()).matches("\\d{6}");
        }

        @Test
        @DisplayName("Should throw OrderNotFoundException when order does not exist")
        void shouldThrowOrderNotFoundExceptionWhenOrderDoesNotExist() {
            when(employeeRestaurantPort.getRestaurantIdByEmployeeId(EMPLOYEE_ID))
                    .thenReturn(Optional.of(RESTAURANT_ID));
            when(orderPersistencePort.findById(ORDER_ID))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> orderUseCase.markOrderAsReady(ORDER_ID, EMPLOYEE_ID))
                    .isInstanceOf(com.pragma.plazoleta.domain.exception.OrderNotFoundException.class)
                    .hasMessageContaining(ORDER_ID.toString());

            verify(orderPersistencePort, never()).saveOrder(any());
            verify(notificationPort, never()).sendOrderReadyNotification(anyString(), anyString(), anyString(), anyString());
        }

        @Test
        @DisplayName("Should throw OrderNotInPreparationException when order is PENDING")
        void shouldThrowOrderNotInPreparationExceptionWhenOrderIsPending() {
            Order pendingOrder = createOrderWithStatus(ORDER_ID, RESTAURANT_ID, OrderStatus.PENDING);

            when(employeeRestaurantPort.getRestaurantIdByEmployeeId(EMPLOYEE_ID))
                    .thenReturn(Optional.of(RESTAURANT_ID));
            when(orderPersistencePort.findById(ORDER_ID))
                    .thenReturn(Optional.of(pendingOrder));

            assertThatThrownBy(() -> orderUseCase.markOrderAsReady(ORDER_ID, EMPLOYEE_ID))
                    .isInstanceOf(OrderNotInPreparationException.class)
                    .hasMessageContaining(ORDER_ID.toString());

            verify(orderPersistencePort, never()).saveOrder(any());
            verify(notificationPort, never()).sendOrderReadyNotification(anyString(), anyString(), anyString(), anyString());
        }

        @Test
        @DisplayName("Should throw OrderNotInPreparationException when order is READY")
        void shouldThrowOrderNotInPreparationExceptionWhenOrderIsReady() {
            Order readyOrder = createOrderWithStatus(ORDER_ID, RESTAURANT_ID, OrderStatus.READY);

            when(employeeRestaurantPort.getRestaurantIdByEmployeeId(EMPLOYEE_ID))
                    .thenReturn(Optional.of(RESTAURANT_ID));
            when(orderPersistencePort.findById(ORDER_ID))
                    .thenReturn(Optional.of(readyOrder));

            assertThatThrownBy(() -> orderUseCase.markOrderAsReady(ORDER_ID, EMPLOYEE_ID))
                    .isInstanceOf(OrderNotInPreparationException.class);

            verify(orderPersistencePort, never()).saveOrder(any());
        }

        @Test
        @DisplayName("Should throw OrderNotInPreparationException when order is DELIVERED")
        void shouldThrowOrderNotInPreparationExceptionWhenOrderIsDelivered() {
            Order deliveredOrder = createOrderWithStatus(ORDER_ID, RESTAURANT_ID, OrderStatus.DELIVERED);

            when(employeeRestaurantPort.getRestaurantIdByEmployeeId(EMPLOYEE_ID))
                    .thenReturn(Optional.of(RESTAURANT_ID));
            when(orderPersistencePort.findById(ORDER_ID))
                    .thenReturn(Optional.of(deliveredOrder));

            assertThatThrownBy(() -> orderUseCase.markOrderAsReady(ORDER_ID, EMPLOYEE_ID))
                    .isInstanceOf(OrderNotInPreparationException.class);

            verify(orderPersistencePort, never()).saveOrder(any());
        }

        @Test
        @DisplayName("Should throw OrderNotInPreparationException when order is CANCELLED")
        void shouldThrowOrderNotInPreparationExceptionWhenOrderIsCancelled() {
            Order cancelledOrder = createOrderWithStatus(ORDER_ID, RESTAURANT_ID, OrderStatus.CANCELLED);

            when(employeeRestaurantPort.getRestaurantIdByEmployeeId(EMPLOYEE_ID))
                    .thenReturn(Optional.of(RESTAURANT_ID));
            when(orderPersistencePort.findById(ORDER_ID))
                    .thenReturn(Optional.of(cancelledOrder));

            assertThatThrownBy(() -> orderUseCase.markOrderAsReady(ORDER_ID, EMPLOYEE_ID))
                    .isInstanceOf(OrderNotInPreparationException.class);

            verify(orderPersistencePort, never()).saveOrder(any());
        }

        @Test
        @DisplayName("Should throw OrderNotFromEmployeeRestaurantException when order does not belong to employee's restaurant")
        void shouldThrowOrderNotFromEmployeeRestaurantException() {
            Long differentRestaurantId = 999L;
            Order orderFromDifferentRestaurant = createOrderWithStatus(ORDER_ID, differentRestaurantId, OrderStatus.IN_PREPARATION);

            when(employeeRestaurantPort.getRestaurantIdByEmployeeId(EMPLOYEE_ID))
                    .thenReturn(Optional.of(RESTAURANT_ID));
            when(orderPersistencePort.findById(ORDER_ID))
                    .thenReturn(Optional.of(orderFromDifferentRestaurant));

            assertThatThrownBy(() -> orderUseCase.markOrderAsReady(ORDER_ID, EMPLOYEE_ID))
                    .isInstanceOf(com.pragma.plazoleta.domain.exception.OrderNotFromEmployeeRestaurantException.class)
                    .hasMessageContaining(ORDER_ID.toString());

            verify(orderPersistencePort, never()).saveOrder(any());
        }

        @Test
        @DisplayName("Should throw EmployeeNotAssociatedWithRestaurantException when employee has no restaurant")
        void shouldThrowEmployeeNotAssociatedWithRestaurantException() {
            when(employeeRestaurantPort.getRestaurantIdByEmployeeId(EMPLOYEE_ID))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> orderUseCase.markOrderAsReady(ORDER_ID, EMPLOYEE_ID))
                    .isInstanceOf(EmployeeNotAssociatedWithRestaurantException.class)
                    .hasMessageContaining(EMPLOYEE_ID.toString());

            verify(orderPersistencePort, never()).findById(any());
            verify(orderPersistencePort, never()).saveOrder(any());
        }

        @Test
        @DisplayName("Should throw ClientPhoneNotFoundException when client phone is not found")
        void shouldThrowClientPhoneNotFoundExceptionWhenPhoneNotFound() {
            Order inPreparationOrder = createOrderWithStatus(ORDER_ID, RESTAURANT_ID, OrderStatus.IN_PREPARATION);
            inPreparationOrder.setEmployeeId(EMPLOYEE_ID);

            when(employeeRestaurantPort.getRestaurantIdByEmployeeId(EMPLOYEE_ID))
                    .thenReturn(Optional.of(RESTAURANT_ID));
            when(orderPersistencePort.findById(ORDER_ID))
                    .thenReturn(Optional.of(inPreparationOrder));
            when(orderPersistencePort.saveOrder(any(Order.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));
            when(clientInfoPort.getClientPhoneById(CLIENT_ID))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> orderUseCase.markOrderAsReady(ORDER_ID, EMPLOYEE_ID))
                    .isInstanceOf(ClientPhoneNotFoundException.class)
                    .hasMessageContaining(CLIENT_ID.toString());

            verify(notificationPort, never()).sendOrderReadyNotification(anyString(), anyString(), anyString(), anyString());
        }

        @Test
        @DisplayName("Should send notification with correct parameters")
        void shouldSendNotificationWithCorrectParameters() {
            Order inPreparationOrder = createOrderWithStatus(ORDER_ID, RESTAURANT_ID, OrderStatus.IN_PREPARATION);
            inPreparationOrder.setEmployeeId(EMPLOYEE_ID);

            when(employeeRestaurantPort.getRestaurantIdByEmployeeId(EMPLOYEE_ID))
                    .thenReturn(Optional.of(RESTAURANT_ID));
            when(orderPersistencePort.findById(ORDER_ID))
                    .thenReturn(Optional.of(inPreparationOrder));
            when(orderPersistencePort.saveOrder(any(Order.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));
            when(clientInfoPort.getClientPhoneById(CLIENT_ID))
                    .thenReturn(Optional.of(CLIENT_PHONE));
            when(restaurantPersistencePort.findById(RESTAURANT_ID))
                    .thenReturn(Optional.of(restaurant));

            Order result = orderUseCase.markOrderAsReady(ORDER_ID, EMPLOYEE_ID);

            verify(notificationPort).sendOrderReadyNotification(
                    CLIENT_PHONE,
                    ORDER_ID.toString(),
                    result.getSecurityPin(),
                    restaurant.getName()
            );
        }
    }

    @Nested
    @DisplayName("Cancel Order")
    class CancelOrderTests {

        private static final Long ORDER_ID = 300L;

        @Test
        @DisplayName("Should cancel order successfully when order is PENDING and user is owner")
        void shouldCancelOrderSuccessfully() {
            Order pendingOrder = createOrderWithStatus(ORDER_ID, RESTAURANT_ID, OrderStatus.PENDING);

            when(orderPersistencePort.findById(ORDER_ID)).thenReturn(Optional.of(pendingOrder));
            when(orderPersistencePort.saveOrder(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

            orderUseCase.cancelOrder(ORDER_ID, CLIENT_ID);

            assertThat(pendingOrder.getStatus()).isEqualTo(OrderStatus.CANCELLED);
            verify(orderPersistencePort).saveOrder(pendingOrder);
        }

        @Test
        @DisplayName("Should throw OrderNotFoundException when order does not exist")
        void shouldThrowOrderNotFoundException() {
            when(orderPersistencePort.findById(ORDER_ID)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> orderUseCase.cancelOrder(ORDER_ID, CLIENT_ID))
                    .isInstanceOf(com.pragma.plazoleta.domain.exception.OrderNotFoundException.class);

            verify(orderPersistencePort, never()).saveOrder(any());
        }

        @Test
        @DisplayName("Should throw UserNotOwnerException when user is not the owner")
        void shouldThrowUserNotOwnerException() {
            Order pendingOrder = createOrderWithStatus(ORDER_ID, RESTAURANT_ID, OrderStatus.PENDING);
            Long otherClientId = 999L;

            when(orderPersistencePort.findById(ORDER_ID)).thenReturn(Optional.of(pendingOrder));

            assertThatThrownBy(() -> orderUseCase.cancelOrder(ORDER_ID, otherClientId))
                    .isInstanceOf(com.pragma.plazoleta.domain.exception.UserNotOwnerException.class);

            verify(orderPersistencePort, never()).saveOrder(any());
        }

        @Test
        @DisplayName("Should throw OrderNotCancellableException when order is not PENDING")
        void shouldThrowOrderNotCancellableException() {
            Order inPreparationOrder = createOrderWithStatus(ORDER_ID, RESTAURANT_ID, OrderStatus.IN_PREPARATION);

            when(orderPersistencePort.findById(ORDER_ID)).thenReturn(Optional.of(inPreparationOrder));

            assertThatThrownBy(() -> orderUseCase.cancelOrder(ORDER_ID, CLIENT_ID))
                    .isInstanceOf(com.pragma.plazoleta.domain.exception.OrderNotCancellableException.class)
                    .hasMessageContaining("Sorry, your order is already in preparation and cannot be canceled");

            verify(orderPersistencePort, never()).saveOrder(any());
        }
    }
}
