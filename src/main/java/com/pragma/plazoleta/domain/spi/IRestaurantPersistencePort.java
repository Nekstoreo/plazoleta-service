package com.pragma.plazoleta.domain.spi;

import com.pragma.plazoleta.domain.model.PagedResult;
import com.pragma.plazoleta.domain.model.Restaurant;

import java.util.Optional;

public interface IRestaurantPersistencePort {

    Restaurant saveRestaurant(Restaurant restaurant);

    boolean existsByNit(String nit);

    Optional<Restaurant> findById(Long id);

    PagedResult<Restaurant> findAllOrderedByNamePaginated(int page, int size);
}
