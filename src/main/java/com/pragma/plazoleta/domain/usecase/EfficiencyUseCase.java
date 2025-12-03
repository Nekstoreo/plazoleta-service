package com.pragma.plazoleta.domain.usecase;

import com.pragma.plazoleta.domain.api.IEfficiencyServicePort;
import com.pragma.plazoleta.domain.exception.RestaurantNotFoundException;
import com.pragma.plazoleta.domain.exception.UserNotRestaurantOwnerException;
import com.pragma.plazoleta.domain.model.EmployeeRanking;
import com.pragma.plazoleta.domain.model.OrderEfficiency;
import com.pragma.plazoleta.domain.model.Restaurant;
import com.pragma.plazoleta.domain.spi.IRestaurantPersistencePort;
import com.pragma.plazoleta.domain.spi.ITraceabilityPort;

import java.util.List;

public class EfficiencyUseCase implements IEfficiencyServicePort {

    private final ITraceabilityPort traceabilityPort;
    private final IRestaurantPersistencePort restaurantPersistencePort;

    public EfficiencyUseCase(ITraceabilityPort traceabilityPort, 
                             IRestaurantPersistencePort restaurantPersistencePort) {
        this.traceabilityPort = traceabilityPort;
        this.restaurantPersistencePort = restaurantPersistencePort;
    }

    @Override
    public List<OrderEfficiency> getOrdersEfficiencyByRestaurant(Long restaurantId, Long ownerId) {
        validateOwnerIsRestaurantOwner(restaurantId, ownerId);
        return traceabilityPort.getOrdersEfficiencyByRestaurant(restaurantId);
    }

    @Override
    public List<EmployeeRanking> getEmployeeRankingByRestaurant(Long restaurantId, Long ownerId) {
        validateOwnerIsRestaurantOwner(restaurantId, ownerId);
        return traceabilityPort.getEmployeeRankingByRestaurant(restaurantId);
    }

    private void validateOwnerIsRestaurantOwner(Long restaurantId, Long ownerId) {
        Restaurant restaurant = restaurantPersistencePort.findById(restaurantId)
                .orElseThrow(() -> new RestaurantNotFoundException(restaurantId));

        if (!restaurant.getOwnerId().equals(ownerId)) {
            throw new UserNotRestaurantOwnerException(ownerId, restaurantId);
        }
    }
}
