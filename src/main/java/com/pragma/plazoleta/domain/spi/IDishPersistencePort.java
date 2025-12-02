package com.pragma.plazoleta.domain.spi;

import com.pragma.plazoleta.domain.model.Dish;
import com.pragma.plazoleta.domain.model.PagedResult;

import java.util.Optional;

public interface IDishPersistencePort {

    Dish saveDish(Dish dish);

    Optional<Dish> findById(Long id);

    PagedResult<Dish> findActiveDishesByRestaurantId(Long restaurantId, int page, int size);

    PagedResult<Dish> findActiveDishesByRestaurantIdAndCategory(Long restaurantId, String category, int page, int size);
}
