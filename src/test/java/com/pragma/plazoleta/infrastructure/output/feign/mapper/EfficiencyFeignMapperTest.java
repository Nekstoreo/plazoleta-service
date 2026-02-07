package com.pragma.plazoleta.infrastructure.output.feign.mapper;

import com.pragma.plazoleta.domain.model.EmployeeRanking;
import com.pragma.plazoleta.domain.model.OrderEfficiency;
import com.pragma.plazoleta.infrastructure.output.feign.dto.EmployeeRankingResponseDto;
import com.pragma.plazoleta.infrastructure.output.feign.dto.OrderEfficiencyResponseDto;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class EfficiencyFeignMapperTest {

    private final EfficiencyFeignMapper mapper = Mappers.getMapper(EfficiencyFeignMapper.class);

    @Test
    void toOrderEfficiency_shouldMapFields() {
        OrderEfficiencyResponseDto r = new OrderEfficiencyResponseDto(1L,2L,3L,"a@b.com", LocalDateTime.now(), LocalDateTime.now().plusMinutes(5),5L,"FINISHED");

        OrderEfficiency oe = mapper.toOrderEfficiency(r);

        assertThat(oe).isNotNull();
        assertThat(oe.getOrderId()).isEqualTo(r.getOrderId());
        assertThat(oe.getEmployeeEmail()).isEqualTo(r.getEmployeeEmail());
        assertThat(oe.getFinalStatus()).isEqualTo(r.getFinalStatus());
    }

    @Test
    void toOrderEfficiencyList_shouldMapList() {
        OrderEfficiencyResponseDto r1 = new OrderEfficiencyResponseDto(1L,2L,3L,"x@x.com", LocalDateTime.now(), LocalDateTime.now(),1L,"S");
        OrderEfficiencyResponseDto r2 = new OrderEfficiencyResponseDto(2L,2L,3L,"y@y.com", LocalDateTime.now(), LocalDateTime.now(),2L,"F");

        List<OrderEfficiency> list = mapper.toOrderEfficiencyList(List.of(r1, r2));

        assertThat(list).hasSize(2);
        assertThat(list.get(0).getEmployeeEmail()).isEqualTo("x@x.com");
        assertThat(list.get(1).getEmployeeEmail()).isEqualTo("y@y.com");
    }

    @Test
    void toEmployeeRanking_shouldMapFields() {
        EmployeeRankingResponseDto r = new EmployeeRankingResponseDto(10L, "e@e.com", 5L, 20L, 15.5, 1);

        EmployeeRanking er = mapper.toEmployeeRanking(r);

        assertThat(er).isNotNull();
        assertThat(er.getEmployeeId()).isEqualTo(r.getEmployeeId());
        assertThat(er.getEmployeeEmail()).isEqualTo(r.getEmployeeEmail());
        assertThat(er.getRankingPosition()).isEqualTo(r.getRankingPosition());
    }

    @Test
    void toEmployeeRankingList_shouldMapList() {
        EmployeeRankingResponseDto r1 = new EmployeeRankingResponseDto(1L, "a@b.com", 1L, 10L, 12.0, 1);
        EmployeeRankingResponseDto r2 = new EmployeeRankingResponseDto(2L, "c@d.com", 1L, 5L, 13.0, 2);

        List<EmployeeRanking> list = mapper.toEmployeeRankingList(List.of(r1, r2));

        assertThat(list).hasSize(2);
        assertThat(list.get(0).getEmployeeId()).isEqualTo(1L);
    }
}
