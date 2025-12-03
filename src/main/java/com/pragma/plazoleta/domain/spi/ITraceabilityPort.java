package com.pragma.plazoleta.domain.spi;

import com.pragma.plazoleta.domain.model.EmployeeRanking;
import com.pragma.plazoleta.domain.model.OrderEfficiency;
import com.pragma.plazoleta.domain.model.Traceability;

import java.util.List;

public interface ITraceabilityPort {
    void saveTraceability(Traceability traceability);
    
    List<Traceability> getTraceabilityByOrderId(Long orderId);
    
    List<OrderEfficiency> getOrdersEfficiencyByRestaurant(Long restaurantId);

    List<EmployeeRanking> getEmployeeRankingByRestaurant(Long restaurantId);
}
