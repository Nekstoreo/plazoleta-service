package com.pragma.plazoleta.infrastructure.output.jpa.adapter;

import com.pragma.plazoleta.domain.model.Order;
import com.pragma.plazoleta.domain.model.OrderItem;
import com.pragma.plazoleta.domain.spi.IOrderPersistencePort;
import com.pragma.plazoleta.infrastructure.output.jpa.entity.OrderEntity;
import com.pragma.plazoleta.infrastructure.output.jpa.entity.OrderItemEntity;
import com.pragma.plazoleta.infrastructure.output.jpa.entity.OrderStatusEntity;
import com.pragma.plazoleta.infrastructure.output.jpa.mapper.OrderEntityMapper;
import com.pragma.plazoleta.infrastructure.output.jpa.repository.IDishRepository;
import com.pragma.plazoleta.infrastructure.output.jpa.repository.IOrderRepository;
import com.pragma.plazoleta.infrastructure.output.jpa.repository.IRestaurantRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Component
public class OrderJpaAdapter implements IOrderPersistencePort {

    private static final List<OrderStatusEntity> ACTIVE_STATUSES = Arrays.asList(
            OrderStatusEntity.PENDING,
            OrderStatusEntity.IN_PREPARATION,
            OrderStatusEntity.READY
    );

    private final IOrderRepository orderRepository;
    private final IRestaurantRepository restaurantRepository;
    private final IDishRepository dishRepository;
    private final OrderEntityMapper orderEntityMapper;

    public OrderJpaAdapter(IOrderRepository orderRepository,
                           IRestaurantRepository restaurantRepository,
                           IDishRepository dishRepository,
                           OrderEntityMapper orderEntityMapper) {
        this.orderRepository = orderRepository;
        this.restaurantRepository = restaurantRepository;
        this.dishRepository = dishRepository;
        this.orderEntityMapper = orderEntityMapper;
    }

    @Override
    @Transactional
    public Order saveOrder(Order order) {
        OrderEntity orderEntity = orderEntityMapper.toEntity(order);

        orderEntity.setRestaurant(
                restaurantRepository.findById(order.getRestaurantId())
                        .orElseThrow(() -> new IllegalArgumentException(
                                "Restaurant not found with id: " + order.getRestaurantId()))
        );

        for (OrderItem item : order.getItems()) {
            OrderItemEntity itemEntity = orderEntityMapper.toItemEntity(item);
            itemEntity.setDish(
                    dishRepository.findById(item.getDishId())
                            .orElseThrow(() -> new IllegalArgumentException(
                                    "Dish not found with id: " + item.getDishId()))
            );
            orderEntity.addItem(itemEntity);
        }

        OrderEntity savedEntity = orderRepository.save(orderEntity);

        return orderEntityMapper.toDomain(savedEntity);
    }

    @Override
    public boolean existsActiveOrderByClientId(Long clientId) {
        return orderRepository.existsByClientIdAndStatusIn(clientId, ACTIVE_STATUSES);
    }
}
