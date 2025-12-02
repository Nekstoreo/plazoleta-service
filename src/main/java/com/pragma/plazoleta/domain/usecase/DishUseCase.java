package com.pragma.plazoleta.domain.usecase;

import com.pragma.plazoleta.domain.api.IDishServicePort;
import com.pragma.plazoleta.domain.exception.DishNotFoundException;
import com.pragma.plazoleta.domain.exception.InvalidActiveStatusException;
import com.pragma.plazoleta.domain.exception.InvalidPriceException;
import com.pragma.plazoleta.domain.exception.RestaurantNotFoundException;
import com.pragma.plazoleta.domain.exception.UserNotRestaurantOwnerException;
import com.pragma.plazoleta.domain.model.Dish;
import com.pragma.plazoleta.domain.model.PagedResult;
import com.pragma.plazoleta.domain.model.Restaurant;
import com.pragma.plazoleta.domain.spi.IDishPersistencePort;
import com.pragma.plazoleta.domain.spi.IRestaurantPersistencePort;

public class DishUseCase implements IDishServicePort {

    private final IDishPersistencePort dishPersistencePort;
    private final IRestaurantPersistencePort restaurantPersistencePort;

    public DishUseCase(IDishPersistencePort dishPersistencePort,
                       IRestaurantPersistencePort restaurantPersistencePort) {
        this.dishPersistencePort = dishPersistencePort;
        this.restaurantPersistencePort = restaurantPersistencePort;
    }

    @Override
    public Dish createDish(Dish dish, Long ownerId) {
        validatePrice(dish.getPrice());
        
        Restaurant restaurant = restaurantPersistencePort.findById(dish.getRestaurantId())
                .orElseThrow(() -> new RestaurantNotFoundException(dish.getRestaurantId()));
        
        validateOwnership(restaurant, ownerId);
        
        dish.setActive(true);
        
        return dishPersistencePort.saveDish(dish);
    }

    @Override
    public Dish updateDish(Long dishId, Integer price, String description, Long ownerId) {
        validatePrice(price);

        Dish dish = dishPersistencePort.findById(dishId)
                .orElseThrow(() -> new DishNotFoundException(dishId));

        Restaurant restaurant = restaurantPersistencePort.findById(dish.getRestaurantId())
                .orElseThrow(() -> new RestaurantNotFoundException(dish.getRestaurantId()));

        validateOwnership(restaurant, ownerId);

        dish.setPrice(price);
        dish.setDescription(description);

        return dishPersistencePort.saveDish(dish);
    }

    @Override
    public Dish changeDishActiveStatus(Long dishId, Boolean active, Long ownerId) {
        if (active == null) {
            throw new InvalidActiveStatusException();
        }

        Dish dish = dishPersistencePort.findById(dishId)
                .orElseThrow(() -> new DishNotFoundException(dishId));

        Restaurant restaurant = restaurantPersistencePort.findById(dish.getRestaurantId())
                .orElseThrow(() -> new RestaurantNotFoundException(dish.getRestaurantId()));

        validateOwnership(restaurant, ownerId);

        dish.setActive(active);

        return dishPersistencePort.saveDish(dish);
    }

    @Override
    public PagedResult<Dish> getDishesByRestaurant(Long restaurantId, String category, int page, int size) {
        restaurantPersistencePort.findById(restaurantId)
                .orElseThrow(() -> new RestaurantNotFoundException(restaurantId));

        if (category != null && !category.isBlank()) {
            return dishPersistencePort.findActiveDishesByRestaurantIdAndCategory(
                    restaurantId, category.trim(), page, size);
        }
        
        return dishPersistencePort.findActiveDishesByRestaurantId(restaurantId, page, size);
    }

    private void validatePrice(Integer price) {
        if (price == null || price <= 0) {
            throw new InvalidPriceException();
        }
    }

    private void validateOwnership(Restaurant restaurant, Long ownerId) {
        if (!restaurant.getOwnerId().equals(ownerId)) {
            throw new UserNotRestaurantOwnerException(ownerId, restaurant.getId());
        }
    }
}
