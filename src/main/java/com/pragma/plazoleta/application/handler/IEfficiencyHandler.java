package com.pragma.plazoleta.application.handler;

import com.pragma.plazoleta.application.dto.response.EmployeeRankingResponseDto;
import com.pragma.plazoleta.application.dto.response.OrderEfficiencyResponseDto;

import java.util.List;

public interface IEfficiencyHandler {
    List<OrderEfficiencyResponseDto> getOrdersEfficiencyByRestaurant(Long restaurantId, Long ownerId);

    List<EmployeeRankingResponseDto> getEmployeeRankingByRestaurant(Long restaurantId, Long ownerId);
}
