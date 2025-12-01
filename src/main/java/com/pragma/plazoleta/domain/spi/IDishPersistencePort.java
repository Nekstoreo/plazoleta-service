package com.pragma.plazoleta.domain.spi;

import com.pragma.plazoleta.domain.model.Dish;

import java.util.Optional;

public interface IDishPersistencePort {

    Dish saveDish(Dish dish);

    Optional<Dish> findById(Long id);
}
