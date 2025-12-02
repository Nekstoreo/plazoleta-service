package com.pragma.plazoleta.domain.api;

import com.pragma.plazoleta.domain.model.PagedResult;
import com.pragma.plazoleta.domain.model.Restaurant;

public interface IRestaurantServicePort {

    Restaurant createRestaurant(Restaurant restaurant);

    PagedResult<Restaurant> getAllRestaurants(int page, int size);
}
