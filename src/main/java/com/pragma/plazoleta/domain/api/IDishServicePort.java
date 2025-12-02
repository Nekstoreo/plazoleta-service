package com.pragma.plazoleta.domain.api;

import com.pragma.plazoleta.domain.model.Dish;
import com.pragma.plazoleta.domain.model.PagedResult;

public interface IDishServicePort {

    Dish createDish(Dish dish, Long ownerId);

    Dish updateDish(Long dishId, Integer price, String description, Long ownerId);

    Dish changeDishActiveStatus(Long dishId, Boolean active, Long ownerId);

    PagedResult<Dish> getDishesByRestaurant(Long restaurantId, String category, int page, int size);
}
