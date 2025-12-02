package com.pragma.plazoleta.infrastructure.output.jpa.repository;

import com.pragma.plazoleta.infrastructure.output.jpa.entity.DishEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IDishRepository extends JpaRepository<DishEntity, Long> {

    Page<DishEntity> findByRestaurantIdAndActiveTrue(Long restaurantId, Pageable pageable);

    Page<DishEntity> findByRestaurantIdAndCategoryIgnoreCaseAndActiveTrue(
            Long restaurantId, String category, Pageable pageable);
}
