package com.pragma.plazoleta.infrastructure.output.feign.adapter;

import com.pragma.plazoleta.domain.model.EmployeeRanking;
import com.pragma.plazoleta.domain.model.OrderEfficiency;
import com.pragma.plazoleta.domain.model.Traceability;
import com.pragma.plazoleta.infrastructure.output.feign.client.ITraceabilityFeignClient;
import com.pragma.plazoleta.infrastructure.output.feign.dto.EmployeeRankingResponseDto;
import com.pragma.plazoleta.infrastructure.output.feign.dto.OrderEfficiencyResponseDto;
import com.pragma.plazoleta.infrastructure.output.feign.dto.TraceabilityRequestDto;
import com.pragma.plazoleta.infrastructure.output.feign.dto.TraceabilityResponseDto;
import com.pragma.plazoleta.infrastructure.output.feign.mapper.EfficiencyFeignMapper;
import com.pragma.plazoleta.infrastructure.output.feign.mapper.TraceabilityFeignMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TraceabilityFeignAdapterTest {

    @Mock
    private ITraceabilityFeignClient traceabilityFeignClient;

    @Mock
    private TraceabilityFeignMapper traceabilityFeignMapper;

    @Mock
    private EfficiencyFeignMapper efficiencyFeignMapper;

    @InjectMocks
    private TraceabilityFeignAdapter adapter;

    @Test
    void saveTraceability_ShouldMapAndDelegate() {
        Traceability traceability = new Traceability();
        TraceabilityRequestDto requestDto = new TraceabilityRequestDto();
        when(traceabilityFeignMapper.toRequest(traceability)).thenReturn(requestDto);

        adapter.saveTraceability(traceability);

        verify(traceabilityFeignMapper).toRequest(traceability);
        verify(traceabilityFeignClient).saveTraceability(requestDto);
    }

    @Test
    void getTraceabilityByOrderId_ShouldReturnMappedList() {
        List<TraceabilityResponseDto> responseDtos = List.of(new TraceabilityResponseDto());
        List<Traceability> expected = List.of(new Traceability());
        when(traceabilityFeignClient.getTraceabilityByOrderId(1L)).thenReturn(responseDtos);
        when(traceabilityFeignMapper.toModelList(responseDtos)).thenReturn(expected);

        List<Traceability> result = adapter.getTraceabilityByOrderId(1L);

        assertEquals(1, result.size());
        assertSame(expected, result);
    }

    @Test
    void getOrdersEfficiencyByRestaurant_ShouldReturnMappedList() {
        List<OrderEfficiencyResponseDto> responseDtos = List.of(new OrderEfficiencyResponseDto());
        List<OrderEfficiency> expected = List.of(new OrderEfficiency());
        when(traceabilityFeignClient.getOrdersEfficiencyByRestaurant(2L)).thenReturn(responseDtos);
        when(efficiencyFeignMapper.toOrderEfficiencyList(responseDtos)).thenReturn(expected);

        List<OrderEfficiency> result = adapter.getOrdersEfficiencyByRestaurant(2L);

        assertEquals(1, result.size());
        assertSame(expected, result);
    }

    @Test
    void getEmployeeRankingByRestaurant_ShouldReturnMappedList() {
        List<EmployeeRankingResponseDto> responseDtos = List.of(new EmployeeRankingResponseDto());
        List<EmployeeRanking> expected = List.of(new EmployeeRanking());
        when(traceabilityFeignClient.getEmployeeRankingByRestaurant(3L)).thenReturn(responseDtos);
        when(efficiencyFeignMapper.toEmployeeRankingList(responseDtos)).thenReturn(expected);

        List<EmployeeRanking> result = adapter.getEmployeeRankingByRestaurant(3L);

        assertEquals(1, result.size());
        assertSame(expected, result);
    }
}
