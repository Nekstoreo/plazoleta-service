package com.pragma.plazoleta.domain.usecase;

import com.pragma.plazoleta.domain.api.IOrderServicePort;
import com.pragma.plazoleta.domain.exception.*;
import com.pragma.plazoleta.domain.model.*;
import com.pragma.plazoleta.domain.spi.*;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;

public class OrderUseCase implements IOrderServicePort {

    private static final int PIN_LENGTH = 6;
    private static final String PIN_CHARACTERS = "0123456789";
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final IOrderPersistencePort orderPersistencePort;
    private final IRestaurantPersistencePort restaurantPersistencePort;
    private final IDishPersistencePort dishPersistencePort;
    private final IEmployeeRestaurantPort employeeRestaurantPort;
    private final IClientInfoPort clientInfoPort;
    private final INotificationPort notificationPort;
    private final ITraceabilityPort traceabilityPort;

    public OrderUseCase(IOrderPersistencePort orderPersistencePort,
            IRestaurantPersistencePort restaurantPersistencePort,
            IDishPersistencePort dishPersistencePort,
            IEmployeeRestaurantPort employeeRestaurantPort,
            IClientInfoPort clientInfoPort,
            INotificationPort notificationPort,
            ITraceabilityPort traceabilityPort) {
        this.orderPersistencePort = orderPersistencePort;
        this.restaurantPersistencePort = restaurantPersistencePort;
        this.dishPersistencePort = dishPersistencePort;
        this.employeeRestaurantPort = employeeRestaurantPort;
        this.clientInfoPort = clientInfoPort;
        this.notificationPort = notificationPort;
        this.traceabilityPort = traceabilityPort;
    }

    @Override
    public Order createOrder(Order order) {
        validateOrderNotEmpty(order.getItems());
        validateRestaurantExists(order.getRestaurantId());
        validateClientHasNoActiveOrder(order.getClientId());
        validateOrderItems(order.getItems(), order.getRestaurantId());

        order.setStatus(OrderStatus.PENDING);
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());

        Order savedOrder = orderPersistencePort.saveOrder(order);
        saveTraceability(savedOrder, null, OrderStatus.PENDING, null);
        return savedOrder;
    }

    @Override
    public PagedResult<Order> getOrdersByRestaurantAndStatus(Long employeeId, OrderStatus status, int page, int size) {
        Long restaurantId = getEmployeeRestaurantId(employeeId);
        return orderPersistencePort.findByRestaurantIdAndStatusPaginated(restaurantId, status, page, size);
    }

    @Override
    public Order assignOrderToEmployee(Long orderId, Long employeeId) {
        Long restaurantId = getEmployeeRestaurantId(employeeId);
        Order order = orderPersistencePort.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        validateOrderBelongsToRestaurant(order, restaurantId);
        validateOrderIsPending(order);

        order.setEmployeeId(employeeId);
        order.setStatus(OrderStatus.IN_PREPARATION);
        order.setUpdatedAt(LocalDateTime.now());

        Order savedOrder = orderPersistencePort.saveOrder(order);
        saveTraceability(savedOrder, OrderStatus.PENDING, OrderStatus.IN_PREPARATION, employeeId);
        return savedOrder;
    }

    @Override
    public Order markOrderAsReady(Long orderId, Long employeeId) {
        Long restaurantId = getEmployeeRestaurantId(employeeId);
        Order order = orderPersistencePort.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        validateOrderBelongsToRestaurant(order, restaurantId);
        validateOrderIsInPreparation(order);

        String securityPin = generateSecurityPin();
        order.setSecurityPin(securityPin);
        order.setStatus(OrderStatus.READY);
        order.setUpdatedAt(LocalDateTime.now());

        Order savedOrder = orderPersistencePort.saveOrder(order);

        saveTraceability(savedOrder, OrderStatus.IN_PREPARATION, OrderStatus.READY, employeeId);
        sendOrderReadyNotification(savedOrder);

        return savedOrder;
    }

    @Override
    public Order markOrderAsDelivered(Long orderId, Long employeeId, String securityPin) {
        Long restaurantId = getEmployeeRestaurantId(employeeId);
        Order order = orderPersistencePort.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        validateOrderBelongsToRestaurant(order, restaurantId);
        validateOrderIsReady(order);
        validateSecurityPin(order, securityPin);

        order.setStatus(OrderStatus.DELIVERED);
        order.setUpdatedAt(LocalDateTime.now());

        Order savedOrder = orderPersistencePort.saveOrder(order);
        saveTraceability(savedOrder, OrderStatus.READY, OrderStatus.DELIVERED, employeeId);
        return savedOrder;
    }

    @Override
    public void cancelOrder(Long orderId, Long clientId) {
        Order order = orderPersistencePort.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        if (!order.getClientId().equals(clientId)) {
            throw new UserNotOwnerException("User is not the owner of the order");
        }

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new OrderNotCancellableException("Lo sentimos, tu pedido ya está en preparación y no puede cancelarse");
        }

        order.setStatus(OrderStatus.CANCELLED);
        order.setUpdatedAt(LocalDateTime.now());
        orderPersistencePort.saveOrder(order);
        
        saveTraceability(order, OrderStatus.PENDING, OrderStatus.CANCELLED, null);
    }

    @Override
    public List<Traceability> getTraceabilityByOrderId(Long orderId, Long clientId) {
        Order order = orderPersistencePort.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        if (!order.getClientId().equals(clientId)) {
            throw new UserNotOwnerException("User is not the owner of the order");
        }

        return traceabilityPort.getTraceabilityByOrderId(orderId);
    }

    private void saveTraceability(Order order, OrderStatus previousStatus, OrderStatus newStatus, Long employeeId) {
        Traceability traceability = new Traceability();
        traceability.setOrderId(order.getId());
        traceability.setClientId(order.getClientId());
        traceability.setClientEmail(clientInfoPort.getClientEmailById(order.getClientId()).orElse(null));
        traceability.setPreviousStatus(previousStatus != null ? previousStatus.name() : null);
        traceability.setNewStatus(newStatus.name());
        traceability.setEmployeeId(employeeId);
        traceability.setRestaurantId(order.getRestaurantId());
        if (employeeId != null) {
            traceability.setEmployeeEmail(employeeRestaurantPort.getEmployeeEmailById(employeeId).orElse(null));
        }
        traceabilityPort.saveTraceability(traceability);
    }

    private void sendOrderReadyNotification(Order order) {
        String clientPhone = clientInfoPort.getClientPhoneById(order.getClientId())
                .orElseThrow(() -> new ClientPhoneNotFoundException(order.getClientId()));

        validatePhoneNumber(clientPhone);

        Restaurant restaurant = restaurantPersistencePort.findById(order.getRestaurantId())
                .orElseThrow(() -> new RestaurantNotFoundException(order.getRestaurantId()));

        notificationPort.sendOrderReadyNotification(
                clientPhone,
                order.getId().toString(),
                order.getSecurityPin(),
                restaurant.getName());
    }

    private String generateSecurityPin() {
        StringBuilder pin = new StringBuilder(PIN_LENGTH);
        for (int i = 0; i < PIN_LENGTH; i++) {
            int randomIndex = SECURE_RANDOM.nextInt(PIN_CHARACTERS.length());
            pin.append(PIN_CHARACTERS.charAt(randomIndex));
        }
        return pin.toString();
    }

    private void validateOrderIsInPreparation(Order order) {
        if (order.getStatus() != OrderStatus.IN_PREPARATION) {
            throw new OrderNotInPreparationException(order.getId());
        }
    }

    private void validateOrderBelongsToRestaurant(Order order, Long restaurantId) {
        if (!order.getRestaurantId().equals(restaurantId)) {
            throw new OrderNotFromEmployeeRestaurantException(order.getId(), restaurantId);
        }
    }

    private void validateOrderIsPending(Order order) {
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new InvalidOrderStatusException("Cannot assign order with id " + order.getId() + " because it is in "
                    + order.getStatus() + " status. Only PENDING orders can be assigned.");
        }
    }

    private void validateOrderIsReady(Order order) {
        if (order.getStatus() != OrderStatus.READY) {
            throw new InvalidOrderStatusException("Cannot deliver order with id " + order.getId() + " because it is in "
                    + order.getStatus() + " status. Only READY orders can be delivered.");
        }
    }

    private void validateSecurityPin(Order order, String securityPin) {
        if (order.getSecurityPin() == null || !order.getSecurityPin().equals(securityPin)) {
            throw new InvalidSecurityPinException("The security PIN provided does not match the order's PIN.");
        }
    }

    private Long getEmployeeRestaurantId(Long employeeId) {
        return employeeRestaurantPort.getRestaurantIdByEmployeeId(employeeId)
                .orElseThrow(() -> new EmployeeNotAssociatedWithRestaurantException(employeeId));
    }

    private void validateOrderNotEmpty(List<OrderItem> items) {
        if (items == null || items.isEmpty()) {
            throw new EmptyOrderException();
        }
    }

    private void validateRestaurantExists(Long restaurantId) {
        restaurantPersistencePort.findById(restaurantId)
                .orElseThrow(() -> new RestaurantNotFoundException(restaurantId));
    }

    private void validateClientHasNoActiveOrder(Long clientId) {
        if (orderPersistencePort.existsActiveOrderByClientId(clientId)) {
            throw new ClientHasActiveOrderException(clientId);
        }
    }

    private void validateOrderItems(List<OrderItem> items, Long restaurantId) {
        for (OrderItem item : items) {
            validateQuantity(item.getQuantity());
            validateDish(item.getDishId(), restaurantId);
        }
    }

    private void validateQuantity(Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new InvalidQuantityException();
        }
    }

    private void validateDish(Long dishId, Long restaurantId) {
        Dish dish = dishPersistencePort.findById(dishId)
                .orElseThrow(() -> new DishNotFoundException(dishId));

        if (!dish.getRestaurantId().equals(restaurantId)) {
            throw new DishNotFromRestaurantException(dishId, restaurantId);
        }

        if (dish.getActive() == null || !dish.getActive()) {
            throw new DishNotActiveException(dishId);
        }
    }

    private void validatePhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Phone number cannot be empty");
        }
        if (!phoneNumber.matches("^\\+[1-9]\\d{1,14}$")) {
            throw new IllegalArgumentException(
                    "Phone number must be in E.164 format (e.g., +573001234567). Received: " + phoneNumber);
        }
    }
}
