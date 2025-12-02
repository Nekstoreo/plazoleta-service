package com.pragma.plazoleta.infrastructure.output.jpa.repository;

import com.pragma.plazoleta.infrastructure.output.jpa.entity.OrderEntity;
import com.pragma.plazoleta.infrastructure.output.jpa.entity.OrderStatusEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IOrderRepository extends JpaRepository<OrderEntity, Long> {

    boolean existsByClientIdAndStatusIn(Long clientId, List<OrderStatusEntity> statuses);

    Page<OrderEntity> findByRestaurantIdAndStatus(Long restaurantId, OrderStatusEntity status, Pageable pageable);
}
