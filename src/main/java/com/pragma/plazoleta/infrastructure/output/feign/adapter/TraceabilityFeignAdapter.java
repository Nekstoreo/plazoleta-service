package com.pragma.plazoleta.infrastructure.output.feign.adapter;

import com.pragma.plazoleta.domain.model.EmployeeRanking;
import com.pragma.plazoleta.domain.model.OrderEfficiency;
import com.pragma.plazoleta.domain.model.Traceability;
import com.pragma.plazoleta.domain.spi.ITraceabilityPort;
import com.pragma.plazoleta.infrastructure.output.feign.client.ITraceabilityFeignClient;
import com.pragma.plazoleta.infrastructure.output.feign.mapper.EfficiencyFeignMapper;
import com.pragma.plazoleta.infrastructure.output.feign.mapper.TraceabilityFeignMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TraceabilityFeignAdapter implements ITraceabilityPort {

    private final ITraceabilityFeignClient traceabilityFeignClient;
    private final TraceabilityFeignMapper traceabilityFeignMapper;
    private final EfficiencyFeignMapper efficiencyFeignMapper;

    @Override
    public void saveTraceability(Traceability traceability) {
        traceabilityFeignClient.saveTraceability(traceabilityFeignMapper.toRequest(traceability));
    }

    @Override
    public List<Traceability> getTraceabilityByOrderId(Long orderId) {
        return traceabilityFeignMapper.toModelList(traceabilityFeignClient.getTraceabilityByOrderId(orderId));
    }

    @Override
    public List<OrderEfficiency> getOrdersEfficiencyByRestaurant(Long restaurantId) {
        return efficiencyFeignMapper.toOrderEfficiencyList(
                traceabilityFeignClient.getOrdersEfficiencyByRestaurant(restaurantId)
        );
    }

    @Override
    public List<EmployeeRanking> getEmployeeRankingByRestaurant(Long restaurantId) {
        return efficiencyFeignMapper.toEmployeeRankingList(
                traceabilityFeignClient.getEmployeeRankingByRestaurant(restaurantId)
        );
    }
}
