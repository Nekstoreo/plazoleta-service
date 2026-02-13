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
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DishUseCase implements IDishServicePort {

    private final IDishPersistencePort dishPersistencePort;
    private final IRestaurantPersistencePort restaurantPersistencePort;

    @Override
    public Dish createDish(Dish dish, Long ownerId) {
        validatePrice(dish.getPrice());
        validateRestaurantOwnership(dish.getRestaurantId(), ownerId);
        
        dish.setActive(true);
        return dishPersistencePort.saveDish(dish);
    }

    @Override
    public Dish updateDish(Long dishId, Integer price, String description, Long ownerId) {
        validatePrice(price);

        Dish dish = findAndValidateDishOwnership(dishId, ownerId);
        dish.setPrice(price);
        dish.setDescription(description);

        return dishPersistencePort.saveDish(dish);
    }

    @Override
    public Dish changeDishActiveStatus(Long dishId, Boolean active, Long ownerId) {
        if (active == null) {
            throw new InvalidActiveStatusException();
        }

        Dish dish = findAndValidateDishOwnership(dishId, ownerId);
        dish.setActive(active);

        return dishPersistencePort.saveDish(dish);
    }

    private Dish findAndValidateDishOwnership(Long dishId, Long ownerId) {
        Dish dish = dishPersistencePort.findById(dishId)
                .orElseThrow(() -> new DishNotFoundException(dishId));
        validateRestaurantOwnership(dish.getRestaurantId(), ownerId);
        return dish;
    }

    private void validateRestaurantOwnership(Long restaurantId, Long ownerId) {
        Restaurant restaurant = restaurantPersistencePort.findById(restaurantId)
                .orElseThrow(() -> new RestaurantNotFoundException(restaurantId));

        if (!restaurant.getOwnerId().equals(ownerId)) {
            throw new UserNotRestaurantOwnerException(ownerId, restaurant.getId());
        }
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
}
