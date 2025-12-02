package com.pragma.plazoleta.application.handler;

import com.pragma.plazoleta.application.dto.request.CreateRestaurantRequest;
import com.pragma.plazoleta.application.dto.response.PagedResponse;
import com.pragma.plazoleta.application.dto.response.RestaurantListItemResponse;
import com.pragma.plazoleta.application.dto.response.RestaurantResponse;

public interface IRestaurantHandler {

    RestaurantResponse createRestaurant(CreateRestaurantRequest request);

    PagedResponse<RestaurantListItemResponse> getAllRestaurants(int page, int size);
}
