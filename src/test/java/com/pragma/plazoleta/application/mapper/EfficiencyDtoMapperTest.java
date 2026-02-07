package com.pragma.plazoleta.application.mapper;

import com.pragma.plazoleta.application.dto.response.EmployeeRankingResponseDto;

import com.pragma.plazoleta.application.dto.response.OrderEfficiencyResponseDto;
import com.pragma.plazoleta.domain.model.EmployeeRanking;
import com.pragma.plazoleta.domain.model.OrderEfficiency;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class EfficiencyDtoMapperTest {
    private static final String EMAIL_1 = "e@r.com";
    private static final String EMAIL_2 = "a@b.com";
    private static final String EMAIL_3 = "c@d.com";

    private final EfficiencyDtoMapper mapper = Mappers.getMapper(EfficiencyDtoMapper.class);

    @Test
    void toOrderEfficiencyResponse_shouldMapFields() {
        OrderEfficiency oe = new OrderEfficiency(1L, 2L, 3L, EMAIL_1, LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(5), 5L, "COMPLETED");

        OrderEfficiencyResponseDto dto = mapper.toOrderEfficiencyResponse(oe);

        assertThat(dto).isNotNull();
        assertThat(dto.getOrderId()).isEqualTo(oe.getOrderId());
        assertThat(dto.getEmployeeEmail()).isEqualTo(oe.getEmployeeEmail());
        assertThat(dto.getFinalStatus()).isEqualTo(oe.getFinalStatus());
    }

    @Test
    void toOrderEfficiencyResponseList_shouldMapList() {
        OrderEfficiency oe1 = new OrderEfficiency(1L, 2L, 3L, EMAIL_2, LocalDateTime.now(), LocalDateTime.now(), 1L,
                "S");
        OrderEfficiency oe2 = new OrderEfficiency(2L, 2L, 3L, EMAIL_3, LocalDateTime.now(), LocalDateTime.now(), 2L,
                "F");

        List<OrderEfficiencyResponseDto> list = mapper.toOrderEfficiencyResponseList(List.of(oe1, oe2));

        assertThat(list).hasSize(2);
        assertThat(list.get(0).getEmployeeEmail()).isEqualTo(EMAIL_2);
        assertThat(list.get(1).getEmployeeEmail()).isEqualTo(EMAIL_3);
    }

    @Test
    void toEmployeeRankingResponse_shouldMapFields() {
        EmployeeRanking er = new EmployeeRanking(1L, EMAIL_1, 10L, 5L, 15.5, 1);

        EmployeeRankingResponseDto dto = mapper.toEmployeeRankingResponse(er);

        assertThat(dto).isNotNull();
        assertThat(dto.getEmployeeId()).isEqualTo(er.getEmployeeId());
        assertThat(dto.getEmployeeEmail()).isEqualTo(er.getEmployeeEmail());
        assertThat(dto.getRestaurantId()).isEqualTo(er.getRestaurantId());
        assertThat(dto.getTotalOrdersCompleted()).isEqualTo(er.getTotalOrdersCompleted());
        assertThat(dto.getAverageDurationInMinutes()).isEqualTo(er.getAverageDurationInMinutes());
        assertThat(dto.getRankingPosition()).isEqualTo(er.getRankingPosition());
    }

    @Test
    void toEmployeeRankingResponseList_shouldMapList() {
        EmployeeRanking er1 = new EmployeeRanking(1L, EMAIL_2, 10L, 5L, 10.0, 1);
        EmployeeRanking er2 = new EmployeeRanking(2L, EMAIL_3, 10L, 3L, 20.0, 2);

        List<EmployeeRankingResponseDto> list = mapper.toEmployeeRankingResponseList(List.of(er1, er2));

        assertThat(list).hasSize(2);
        assertThat(list.get(0).getEmployeeEmail()).isEqualTo(EMAIL_2);
        assertThat(list.get(1).getEmployeeEmail()).isEqualTo(EMAIL_3);
    }

    @Test
    void toOrderEfficiencyResponse_shouldReturnNull_whenSourceIsNull() {
        assertThat(mapper.toOrderEfficiencyResponse(null)).isNull();
    }

    @Test
    void toEmployeeRankingResponse_shouldReturnNull_whenSourceIsNull() {
        assertThat(mapper.toEmployeeRankingResponse(null)).isNull();
    }

    @Test
    void toOrderEfficiencyResponseList_shouldReturnNull_whenSourceIsNull() {
        assertThat(mapper.toOrderEfficiencyResponseList(null)).isNull();
    }

    @Test
    void toEmployeeRankingResponseList_shouldReturnNull_whenSourceIsNull() {
        assertThat(mapper.toEmployeeRankingResponseList(null)).isNull();
    }
}
