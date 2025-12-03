package com.pragma.plazoleta.application.handler;

import com.pragma.plazoleta.application.dto.request.AssignOrderRequestDto;
import com.pragma.plazoleta.application.dto.request.CreateOrderRequestDto;
import com.pragma.plazoleta.application.dto.request.OrderItemRequestDto;
import com.pragma.plazoleta.application.dto.response.OrderItemResponseDto;
import com.pragma.plazoleta.application.dto.response.OrderResponseDto;
import com.pragma.plazoleta.application.dto.response.PagedResponse;
import com.pragma.plazoleta.application.mapper.OrderDtoMapper;
import com.pragma.plazoleta.domain.api.IOrderServicePort;
import com.pragma.plazoleta.domain.model.Dish;
import com.pragma.plazoleta.domain.model.Order;
import com.pragma.plazoleta.domain.model.OrderItem;
import com.pragma.plazoleta.domain.model.OrderStatus;
import com.pragma.plazoleta.domain.model.PagedResult;
import com.pragma.plazoleta.domain.model.Restaurant;
import com.pragma.plazoleta.domain.spi.IDishPersistencePort;
import com.pragma.plazoleta.domain.spi.IRestaurantPersistencePort;
import org.junit.jupiter.api.BeforeEach;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderHandlerTest {

    @Mock
    private IOrderServicePort orderServicePort;

    @Mock
    private IRestaurantPersistencePort restaurantPersistencePort;

    @Mock
    private IDishPersistencePort dishPersistencePort;

    @Mock
    private OrderDtoMapper orderDtoMapper;

    @InjectMocks
    private OrderHandler orderHandler;

    private static final Long CLIENT_ID = 1L;
    private static final Long EMPLOYEE_ID = 50L;
    private static final Long RESTAURANT_ID = 10L;
    private static final Long DISH_ID = 100L;
    private static final String RESTAURANT_NAME = "Mi Restaurante";
    private static final String DISH_NAME = "Hamburguesa";
    private static final Integer DISH_PRICE = 25000;

    private Restaurant restaurant;
    private Dish dish;
    private Order order;
    private OrderResponseDto orderResponseDto;

    @BeforeEach
    void setUp() {
        restaurant = new Restaurant(
                RESTAURANT_NAME,
                "123456789",
                "Calle 123",
                "+573001234567",
                "https://example.com/logo.jpg",
                5L
        );
        restaurant.setId(RESTAURANT_ID);

        dish = new Dish(
                DISH_NAME,
                DISH_PRICE,
                "Descripción",
                "https://example.com/image.jpg",
                "Categoría",
                RESTAURANT_ID
        );
        dish.setId(DISH_ID);
        dish.setActive(true);

        order = new Order();
        order.setId(1L);
        order.setClientId(CLIENT_ID);
        order.setRestaurantId(RESTAURANT_ID);
        order.setStatus(OrderStatus.PENDING);
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        order.setItems(List.of(new OrderItem(DISH_ID, 2)));

        orderResponseDto = OrderResponseDto.builder()
                .id(1L)
                .clientId(CLIENT_ID)
                .restaurantId(RESTAURANT_ID)
                .status("PENDING")
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }

    @Nested
    @DisplayName("Create Order Tests")
    class CreateOrderTests {

        @Test
        @DisplayName("Should create order successfully")
        void shouldCreateOrderSuccessfully() {
            CreateOrderRequestDto request = new CreateOrderRequestDto();
            request.setRestaurantId(RESTAURANT_ID);
            request.setItems(List.of(new OrderItemRequestDto(DISH_ID, 2)));

            Order inputOrder = new Order();
            inputOrder.setRestaurantId(RESTAURANT_ID);

            OrderItemResponseDto itemResponseDto = OrderItemResponseDto.builder()
                    .id(1L)
                    .dishId(DISH_ID)
                    .quantity(2)
                    .build();

            when(orderDtoMapper.toOrder(request)).thenReturn(inputOrder);
            when(orderDtoMapper.toOrderItemList(request.getItems()))
                    .thenReturn(List.of(new OrderItem(DISH_ID, 2)));
            when(orderServicePort.createOrder(any(Order.class))).thenReturn(order);
            when(orderDtoMapper.toOrderResponseDto(order)).thenReturn(orderResponseDto);
            when(restaurantPersistencePort.findById(RESTAURANT_ID)).thenReturn(Optional.of(restaurant));
            when(orderDtoMapper.toOrderItemResponseDto(any(OrderItem.class))).thenReturn(itemResponseDto);
            when(dishPersistencePort.findById(DISH_ID)).thenReturn(Optional.of(dish));

            OrderResponseDto result = orderHandler.createOrder(request, CLIENT_ID);

            assertThat(result).isNotNull();
            assertThat(result.getRestaurantName()).isEqualTo(RESTAURANT_NAME);
            assertThat(result.getItems()).hasSize(1);

            verify(orderServicePort).createOrder(any(Order.class));
            verify(restaurantPersistencePort).findById(RESTAURANT_ID);
        }
    }

    @Nested
    @DisplayName("Get Orders By Status Tests")
    class GetOrdersByStatusTests {

        @Test
        @DisplayName("Should return paginated orders by status")
        void shouldReturnPaginatedOrdersByStatus() {
            int page = 0;
            int size = 10;
            String status = "PENDING";

            Order order2 = new Order();
            order2.setId(2L);
            order2.setClientId(2L);
            order2.setRestaurantId(RESTAURANT_ID);
            order2.setStatus(OrderStatus.PENDING);
            order2.setCreatedAt(LocalDateTime.now());
            order2.setUpdatedAt(LocalDateTime.now());
            order2.setItems(List.of(new OrderItem(DISH_ID, 1)));

            PagedResult<Order> pagedResult = PagedResult.of(
                    Arrays.asList(order, order2),
                    page,
                    size,
                    2L,
                    1
            );

            OrderResponseDto orderResponseDto2 = OrderResponseDto.builder()
                    .id(2L)
                    .clientId(2L)
                    .restaurantId(RESTAURANT_ID)
                    .status("PENDING")
                    .build();

            OrderItemResponseDto itemResponseDto = OrderItemResponseDto.builder()
                    .id(1L)
                    .dishId(DISH_ID)
                    .quantity(2)
                    .build();

            when(orderServicePort.getOrdersByRestaurantAndStatus(EMPLOYEE_ID, OrderStatus.PENDING, page, size))
                    .thenReturn(pagedResult);
            when(orderDtoMapper.toOrderResponseDto(order)).thenReturn(orderResponseDto);
            when(orderDtoMapper.toOrderResponseDto(order2)).thenReturn(orderResponseDto2);
            when(restaurantPersistencePort.findById(RESTAURANT_ID)).thenReturn(Optional.of(restaurant));
            when(orderDtoMapper.toOrderItemResponseDto(any(OrderItem.class))).thenReturn(itemResponseDto);
            when(dishPersistencePort.findById(DISH_ID)).thenReturn(Optional.of(dish));

            PagedResponse<OrderResponseDto> result = orderHandler.getOrdersByStatus(EMPLOYEE_ID, status, page, size);

            assertThat(result).isNotNull();
            assertThat(result.getContent()).hasSize(2);
            assertThat(result.getPage()).isEqualTo(page);
            assertThat(result.getSize()).isEqualTo(size);
            assertThat(result.getTotalElements()).isEqualTo(2L);
            assertThat(result.getTotalPages()).isEqualTo(1);
            assertThat(result.isFirst()).isTrue();
            assertThat(result.isLast()).isTrue();

            verify(orderServicePort).getOrdersByRestaurantAndStatus(EMPLOYEE_ID, OrderStatus.PENDING, page, size);
        }

        @Test
        @DisplayName("Should return empty result when no orders match")
        void shouldReturnEmptyResultWhenNoOrdersMatch() {
            int page = 0;
            int size = 10;
            String status = "READY";

            PagedResult<Order> emptyResult = PagedResult.of(
                    Collections.emptyList(),
                    page,
                    size,
                    0L,
                    0
            );

            when(orderServicePort.getOrdersByRestaurantAndStatus(EMPLOYEE_ID, OrderStatus.READY, page, size))
                    .thenReturn(emptyResult);

            PagedResponse<OrderResponseDto> result = orderHandler.getOrdersByStatus(EMPLOYEE_ID, status, page, size);

            assertThat(result).isNotNull();
            assertThat(result.getContent()).isEmpty();
            assertThat(result.getTotalElements()).isZero();
        }

        @Test
        @DisplayName("Should handle case insensitive status")
        void shouldHandleCaseInsensitiveStatus() {
            int page = 0;
            int size = 10;
            String status = "pending";

            PagedResult<Order> pagedResult = PagedResult.of(
                    Collections.emptyList(),
                    page,
                    size,
                    0L,
                    0
            );

            when(orderServicePort.getOrdersByRestaurantAndStatus(EMPLOYEE_ID, OrderStatus.PENDING, page, size))
                    .thenReturn(pagedResult);

            PagedResponse<OrderResponseDto> result = orderHandler.getOrdersByStatus(EMPLOYEE_ID, status, page, size);

            assertThat(result).isNotNull();
            verify(orderServicePort).getOrdersByRestaurantAndStatus(EMPLOYEE_ID, OrderStatus.PENDING, page, size);
        }

        @Test
        @DisplayName("Should throw exception for invalid status")
        void shouldThrowExceptionForInvalidStatus() {
            int page = 0;
            int size = 10;
            String invalidStatus = "INVALID_STATUS";

            assertThatThrownBy(() -> orderHandler.getOrdersByStatus(EMPLOYEE_ID, invalidStatus, page, size))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("Should enrich response with restaurant name")
        void shouldEnrichResponseWithRestaurantName() {
            int page = 0;
            int size = 10;
            String status = "PENDING";

            PagedResult<Order> pagedResult = PagedResult.of(
                    List.of(order),
                    page,
                    size,
                    1L,
                    1
            );

            OrderItemResponseDto itemResponseDto = OrderItemResponseDto.builder()
                    .id(1L)
                    .dishId(DISH_ID)
                    .quantity(2)
                    .build();

            when(orderServicePort.getOrdersByRestaurantAndStatus(EMPLOYEE_ID, OrderStatus.PENDING, page, size))
                    .thenReturn(pagedResult);
            when(orderDtoMapper.toOrderResponseDto(order)).thenReturn(orderResponseDto);
            when(restaurantPersistencePort.findById(RESTAURANT_ID)).thenReturn(Optional.of(restaurant));
            when(orderDtoMapper.toOrderItemResponseDto(any(OrderItem.class))).thenReturn(itemResponseDto);
            when(dishPersistencePort.findById(DISH_ID)).thenReturn(Optional.of(dish));

            PagedResponse<OrderResponseDto> result = orderHandler.getOrdersByStatus(EMPLOYEE_ID, status, page, size);

            assertThat(result.getContent().get(0).getRestaurantName()).isEqualTo(RESTAURANT_NAME);
        }

        @Test
        @DisplayName("Should enrich items with dish name and price")
        void shouldEnrichItemsWithDishNameAndPrice() {
            int page = 0;
            int size = 10;
            String status = "PENDING";

            PagedResult<Order> pagedResult = PagedResult.of(
                    List.of(order),
                    page,
                    size,
                    1L,
                    1
            );

            OrderItemResponseDto itemResponseDto = OrderItemResponseDto.builder()
                    .id(1L)
                    .dishId(DISH_ID)
                    .quantity(2)
                    .build();

            when(orderServicePort.getOrdersByRestaurantAndStatus(EMPLOYEE_ID, OrderStatus.PENDING, page, size))
                    .thenReturn(pagedResult);
            when(orderDtoMapper.toOrderResponseDto(order)).thenReturn(orderResponseDto);
            when(restaurantPersistencePort.findById(RESTAURANT_ID)).thenReturn(Optional.of(restaurant));
            when(orderDtoMapper.toOrderItemResponseDto(any(OrderItem.class))).thenReturn(itemResponseDto);
            when(dishPersistencePort.findById(DISH_ID)).thenReturn(Optional.of(dish));

            PagedResponse<OrderResponseDto> result = orderHandler.getOrdersByStatus(EMPLOYEE_ID, status, page, size);

            OrderItemResponseDto item = result.getContent().get(0).getItems().get(0);
            assertThat(item.getDishName()).isEqualTo(DISH_NAME);
            assertThat(item.getDishPrice()).isEqualTo(DISH_PRICE);
        }

        @Test
        @DisplayName("Should include all order fields in response")
        void shouldIncludeAllOrderFieldsInResponse() {
            int page = 0;
            int size = 10;
            String status = "PENDING";

            order.setEmployeeId(EMPLOYEE_ID);
            order.setSecurityPin("123456");

            PagedResult<Order> pagedResult = PagedResult.of(
                    List.of(order),
                    page,
                    size,
                    1L,
                    1
            );

            OrderItemResponseDto itemResponseDto = OrderItemResponseDto.builder()
                    .id(1L)
                    .dishId(DISH_ID)
                    .quantity(2)
                    .build();

            OrderResponseDto responseDto = OrderResponseDto.builder()
                    .id(order.getId())
                    .clientId(order.getClientId())
                    .restaurantId(order.getRestaurantId())
                    .employeeId(order.getEmployeeId())
                    .status(order.getStatus().name())
                    .createdAt(order.getCreatedAt())
                    .updatedAt(order.getUpdatedAt())
                    .securityPin(order.getSecurityPin())
                    .build();

            when(orderServicePort.getOrdersByRestaurantAndStatus(EMPLOYEE_ID, OrderStatus.PENDING, page, size))
                    .thenReturn(pagedResult);
            when(orderDtoMapper.toOrderResponseDto(order)).thenReturn(responseDto);
            when(restaurantPersistencePort.findById(RESTAURANT_ID)).thenReturn(Optional.of(restaurant));
            when(orderDtoMapper.toOrderItemResponseDto(any(OrderItem.class))).thenReturn(itemResponseDto);
            when(dishPersistencePort.findById(DISH_ID)).thenReturn(Optional.of(dish));

            PagedResponse<OrderResponseDto> result = orderHandler.getOrdersByStatus(EMPLOYEE_ID, status, page, size);

            OrderResponseDto returnedOrder = result.getContent().get(0);
            assertThat(returnedOrder.getId()).isEqualTo(order.getId());
            assertThat(returnedOrder.getClientId()).isEqualTo(order.getClientId());
            assertThat(returnedOrder.getRestaurantId()).isEqualTo(order.getRestaurantId());
            assertThat(returnedOrder.getEmployeeId()).isEqualTo(order.getEmployeeId());
            assertThat(returnedOrder.getStatus()).isEqualTo(order.getStatus().name());
            assertThat(returnedOrder.getCreatedAt()).isEqualTo(order.getCreatedAt());
            assertThat(returnedOrder.getUpdatedAt()).isEqualTo(order.getUpdatedAt());
            assertThat(returnedOrder.getSecurityPin()).isEqualTo(order.getSecurityPin());
        }

        @Test
        @DisplayName("Should handle pagination metadata correctly")
        void shouldHandlePaginationMetadataCorrectly() {
            int page = 1;
            int size = 5;
            String status = "IN_PREPARATION";

            PagedResult<Order> pagedResult = PagedResult.of(
                    List.of(order),
                    page,
                    size,
                    25L,
                    5
            );

            OrderItemResponseDto itemResponseDto = OrderItemResponseDto.builder()
                    .id(1L)
                    .dishId(DISH_ID)
                    .quantity(2)
                    .build();

            when(orderServicePort.getOrdersByRestaurantAndStatus(EMPLOYEE_ID, OrderStatus.IN_PREPARATION, page, size))
                    .thenReturn(pagedResult);
            when(orderDtoMapper.toOrderResponseDto(order)).thenReturn(orderResponseDto);
            when(restaurantPersistencePort.findById(RESTAURANT_ID)).thenReturn(Optional.of(restaurant));
            when(orderDtoMapper.toOrderItemResponseDto(any(OrderItem.class))).thenReturn(itemResponseDto);
            when(dishPersistencePort.findById(DISH_ID)).thenReturn(Optional.of(dish));

            PagedResponse<OrderResponseDto> result = orderHandler.getOrdersByStatus(EMPLOYEE_ID, status, page, size);

            assertThat(result.getPage()).isEqualTo(page);
            assertThat(result.getSize()).isEqualTo(size);
            assertThat(result.getTotalElements()).isEqualTo(25L);
            assertThat(result.getTotalPages()).isEqualTo(5);
            assertThat(result.isFirst()).isFalse();
            assertThat(result.isLast()).isFalse();
        }
    }

    @Nested
    @DisplayName("Assign Order To Employee")
    class AssignOrderToEmployeeTests {

        private static final Long ORDER_ID = 200L;

        @Test
        @DisplayName("Should assign order to employee successfully")
        void shouldAssignOrderToEmployeeSuccessfully() {
            AssignOrderRequestDto request = new AssignOrderRequestDto(ORDER_ID);
            
            Order assignedOrder = new Order();
            assignedOrder.setId(ORDER_ID);
            assignedOrder.setClientId(CLIENT_ID);
            assignedOrder.setRestaurantId(RESTAURANT_ID);
            assignedOrder.setEmployeeId(EMPLOYEE_ID);
            assignedOrder.setStatus(OrderStatus.IN_PREPARATION);
            assignedOrder.setCreatedAt(LocalDateTime.now());
            assignedOrder.setUpdatedAt(LocalDateTime.now());
            OrderItem item = new OrderItem();
            item.setDishId(DISH_ID);
            item.setQuantity(2);
            assignedOrder.setItems(Arrays.asList(item));

            when(orderServicePort.assignOrderToEmployee(ORDER_ID, EMPLOYEE_ID))
                    .thenReturn(assignedOrder);
            when(restaurantPersistencePort.findById(RESTAURANT_ID))
                    .thenReturn(Optional.of(restaurant));
            when(dishPersistencePort.findById(DISH_ID))
                    .thenReturn(Optional.of(dish));
            when(orderDtoMapper.toOrderResponseDto(assignedOrder))
                    .thenReturn(orderResponseDto);
            when(orderDtoMapper.toOrderItemResponseDto(any(OrderItem.class)))
                    .thenReturn(new OrderItemResponseDto(1L, DISH_ID, DISH_NAME, DISH_PRICE, 2));

            OrderResponseDto result = orderHandler.assignOrderToEmployee(request, EMPLOYEE_ID);

            assertThat(result).isNotNull();
            verify(orderServicePort).assignOrderToEmployee(ORDER_ID, EMPLOYEE_ID);
            verify(restaurantPersistencePort).findById(RESTAURANT_ID);
        }

        @Test
        @DisplayName("Should throw exception when assigning non-existent order")
        void shouldThrowExceptionWhenAssigningNonExistentOrder() {
            AssignOrderRequestDto request = new AssignOrderRequestDto(ORDER_ID);

            when(orderServicePort.assignOrderToEmployee(ORDER_ID, EMPLOYEE_ID))
                    .thenThrow(new com.pragma.plazoleta.domain.exception.OrderNotFoundException(ORDER_ID));

            assertThatThrownBy(() -> orderHandler.assignOrderToEmployee(request, EMPLOYEE_ID))
                    .isInstanceOf(com.pragma.plazoleta.domain.exception.OrderNotFoundException.class);

            verify(orderServicePort).assignOrderToEmployee(ORDER_ID, EMPLOYEE_ID);
        }

        @Test
        @DisplayName("Should throw exception when order status is not PENDING")
        void shouldThrowExceptionWhenOrderStatusIsNotPending() {
            AssignOrderRequestDto request = new AssignOrderRequestDto(ORDER_ID);

            when(orderServicePort.assignOrderToEmployee(ORDER_ID, EMPLOYEE_ID))
                    .thenThrow(new com.pragma.plazoleta.domain.exception.InvalidOrderStatusException(ORDER_ID, "IN_PREPARATION"));

            assertThatThrownBy(() -> orderHandler.assignOrderToEmployee(request, EMPLOYEE_ID))
                    .isInstanceOf(com.pragma.plazoleta.domain.exception.InvalidOrderStatusException.class);

            verify(orderServicePort).assignOrderToEmployee(ORDER_ID, EMPLOYEE_ID);
        }

        @Test
        @DisplayName("Should throw exception when order does not belong to employee's restaurant")
        void shouldThrowExceptionWhenOrderDoesNotBelongToRestaurant() {
            AssignOrderRequestDto request = new AssignOrderRequestDto(ORDER_ID);

            when(orderServicePort.assignOrderToEmployee(ORDER_ID, EMPLOYEE_ID))
                    .thenThrow(new com.pragma.plazoleta.domain.exception.OrderNotFromEmployeeRestaurantException(ORDER_ID, 999L));

            assertThatThrownBy(() -> orderHandler.assignOrderToEmployee(request, EMPLOYEE_ID))
                    .isInstanceOf(com.pragma.plazoleta.domain.exception.OrderNotFromEmployeeRestaurantException.class);

            verify(orderServicePort).assignOrderToEmployee(ORDER_ID, EMPLOYEE_ID);
        }
    }}