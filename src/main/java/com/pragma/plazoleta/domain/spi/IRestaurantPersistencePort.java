package com.pragma.plazoleta.domain.spi;

import com.pragma.plazoleta.domain.model.Restaurant;

public interface IRestaurantPersistencePort {

    Restaurant saveRestaurant(Restaurant restaurant);

    boolean existsByNit(String nit);
}
