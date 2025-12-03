package com.pragma.plazoleta.application.handler;

import com.pragma.plazoleta.application.dto.response.EmployeeRankingResponseDto;
import com.pragma.plazoleta.application.dto.response.OrderEfficiencyResponseDto;
import com.pragma.plazoleta.application.mapper.EfficiencyDtoMapper;
import com.pragma.plazoleta.domain.api.IEfficiencyServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class EfficiencyHandler implements IEfficiencyHandler {

    private final IEfficiencyServicePort efficiencyServicePort;
    private final EfficiencyDtoMapper efficiencyDtoMapper;

    @Override
    public List<OrderEfficiencyResponseDto> getOrdersEfficiencyByRestaurant(Long restaurantId, Long ownerId) {
        return efficiencyDtoMapper.toOrderEfficiencyResponseList(
                efficiencyServicePort.getOrdersEfficiencyByRestaurant(restaurantId, ownerId)
        );
    }

    @Override
    public List<EmployeeRankingResponseDto> getEmployeeRankingByRestaurant(Long restaurantId, Long ownerId) {
        return efficiencyDtoMapper.toEmployeeRankingResponseList(
                efficiencyServicePort.getEmployeeRankingByRestaurant(restaurantId, ownerId)
        );
    }
}
