package com.pragma.plazoleta.application.handler;

import com.pragma.plazoleta.application.dto.request.CreateRestaurantRequest;
import com.pragma.plazoleta.application.dto.response.PagedResponse;
import com.pragma.plazoleta.application.dto.response.RestaurantListItemResponse;
import com.pragma.plazoleta.application.dto.response.RestaurantResponse;
import com.pragma.plazoleta.application.mapper.RestaurantRequestMapper;
import com.pragma.plazoleta.application.mapper.RestaurantResponseMapper;
import com.pragma.plazoleta.domain.api.IRestaurantServicePort;
import com.pragma.plazoleta.domain.model.PagedResult;
import com.pragma.plazoleta.domain.model.Restaurant;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<RestaurantListItemResponse> getAllRestaurants(int page, int size) {
        PagedResult<Restaurant> pagedResult = restaurantServicePort.getAllRestaurants(page, size);
        
        List<RestaurantListItemResponse> restaurantList = pagedResult.getContent().stream()
                .map(restaurantResponseMapper::toListItemResponse)
                .toList();

        return PagedResponse.<RestaurantListItemResponse>builder()
                .content(restaurantList)
                .page(pagedResult.getPage())
                .size(pagedResult.getSize())
                .totalElements(pagedResult.getTotalElements())
                .totalPages(pagedResult.getTotalPages())
                .first(pagedResult.isFirst())
                .last(pagedResult.isLast())
                .build();
    }
}
