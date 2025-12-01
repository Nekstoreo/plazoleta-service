package com.pragma.plazoleta.application.handler;

import com.pragma.plazoleta.application.dto.request.CreateRestaurantRequest;
import com.pragma.plazoleta.application.dto.response.RestaurantResponse;
import com.pragma.plazoleta.application.mapper.RestaurantRequestMapper;
import com.pragma.plazoleta.application.mapper.RestaurantResponseMapper;
import com.pragma.plazoleta.domain.api.IRestaurantServicePort;
import com.pragma.plazoleta.domain.model.Restaurant;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class RestaurantHandler implements IRestaurantHandler {

    private final IRestaurantServicePort restaurantServicePort;
    private final RestaurantRequestMapper restaurantRequestMapper;
    private final RestaurantResponseMapper restaurantResponseMapper;

    public RestaurantHandler(IRestaurantServicePort restaurantServicePort,
                             RestaurantRequestMapper restaurantRequestMapper,
                             RestaurantResponseMapper restaurantResponseMapper) {
        this.restaurantServicePort = restaurantServicePort;
        this.restaurantRequestMapper = restaurantRequestMapper;
        this.restaurantResponseMapper = restaurantResponseMapper;
    }

    @Override
    public RestaurantResponse createRestaurant(CreateRestaurantRequest request) {
        Restaurant restaurant = restaurantRequestMapper.toRestaurant(request);
        Restaurant savedRestaurant = restaurantServicePort.createRestaurant(restaurant);
        return restaurantResponseMapper.toResponse(savedRestaurant);
    }
}
