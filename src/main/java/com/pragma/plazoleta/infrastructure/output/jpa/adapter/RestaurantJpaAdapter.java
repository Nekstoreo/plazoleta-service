package com.pragma.plazoleta.infrastructure.output.jpa.adapter;

import com.pragma.plazoleta.domain.model.Restaurant;
import com.pragma.plazoleta.domain.spi.IRestaurantPersistencePort;
import com.pragma.plazoleta.infrastructure.output.jpa.entity.RestaurantEntity;
import com.pragma.plazoleta.infrastructure.output.jpa.mapper.RestaurantEntityMapper;
import com.pragma.plazoleta.infrastructure.output.jpa.repository.IRestaurantRepository;
import org.springframework.stereotype.Component;

@Component
public class RestaurantJpaAdapter implements IRestaurantPersistencePort {

    private final IRestaurantRepository restaurantRepository;
    private final RestaurantEntityMapper restaurantEntityMapper;

    public RestaurantJpaAdapter(IRestaurantRepository restaurantRepository,
                                RestaurantEntityMapper restaurantEntityMapper) {
        this.restaurantRepository = restaurantRepository;
        this.restaurantEntityMapper = restaurantEntityMapper;
    }

    @Override
    public Restaurant saveRestaurant(Restaurant restaurant) {
        RestaurantEntity entity = restaurantEntityMapper.toEntity(restaurant);
        RestaurantEntity savedEntity = restaurantRepository.save(entity);
        return restaurantEntityMapper.toRestaurant(savedEntity);
    }

    @Override
    public boolean existsByNit(String nit) {
        return restaurantRepository.existsByNit(nit);
    }
}
