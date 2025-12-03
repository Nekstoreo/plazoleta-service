package com.pragma.plazoleta.domain.api;

import com.pragma.plazoleta.domain.model.EmployeeRanking;
import com.pragma.plazoleta.domain.model.OrderEfficiency;

import java.util.List;

public interface IEfficiencyServicePort {
    List<OrderEfficiency> getOrdersEfficiencyByRestaurant(Long restaurantId, Long ownerId);

    List<EmployeeRanking> getEmployeeRankingByRestaurant(Long restaurantId, Long ownerId);
}
