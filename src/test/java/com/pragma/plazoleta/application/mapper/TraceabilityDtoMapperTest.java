package com.pragma.plazoleta.application.mapper;

import com.pragma.plazoleta.application.dto.response.TraceabilityResponseDto;
import com.pragma.plazoleta.domain.model.Traceability;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class TraceabilityDtoMapperTest {

    private final TraceabilityDtoMapper mapper = Mappers.getMapper(TraceabilityDtoMapper.class);

    @Test
    void toResponse_shouldMapFields() {
        Traceability t = new Traceability();
        t.setId("abc");
        t.setOrderId(10L);
        t.setClientEmail("a@b.com");
        t.setDate(LocalDateTime.now());
        t.setPreviousStatus("PENDING");
        t.setNewStatus("IN_PREPARATION");

        TraceabilityResponseDto dto = mapper.toResponse(t);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(t.getId());
        assertThat(dto.getOrderId()).isEqualTo(t.getOrderId());
        assertThat(dto.getClientEmail()).isEqualTo(t.getClientEmail());
        assertThat(dto.getDate()).isEqualTo(t.getDate());
        assertThat(dto.getPreviousStatus()).isEqualTo(t.getPreviousStatus());
        assertThat(dto.getNewStatus()).isEqualTo(t.getNewStatus());
    }

    @Test
    void toResponseList_shouldMapList() {
        Traceability t1 = new Traceability();
        t1.setId("1");
        Traceability t2 = new Traceability();
        t2.setId("2");

        List<TraceabilityResponseDto> list = mapper.toResponseList(List.of(t1, t2));

        assertThat(list).hasSize(2);
        assertThat(list.get(0).getId()).isEqualTo("1");
        assertThat(list.get(1).getId()).isEqualTo("2");
    }

    @Test
    void toResponse_shouldReturnNull_whenTraceabilityIsNull() {
        assertThat(mapper.toResponse(null)).isNull();
    }

    @Test
    void toResponseList_shouldReturnNull_whenListIsNull() {
        assertThat(mapper.toResponseList(null)).isNull();
    }
}
