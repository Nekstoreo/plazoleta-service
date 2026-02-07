package com.pragma.plazoleta.infrastructure.output.feign.mapper;

import com.pragma.plazoleta.domain.model.Traceability;
import com.pragma.plazoleta.infrastructure.output.feign.dto.TraceabilityRequestDto;
import com.pragma.plazoleta.infrastructure.output.feign.dto.TraceabilityResponseDto;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class TraceabilityFeignMapperTest {

    private final TraceabilityFeignMapper mapper = Mappers.getMapper(TraceabilityFeignMapper.class);

    @Test
    void toRequest_shouldMapFields() {
        Traceability t = new Traceability();
        t.setOrderId(10L);
        t.setClientId(20L);
        t.setClientEmail("c@d.com");
        t.setPreviousStatus("PENDING");
        t.setNewStatus("IN_PREPARATION");
        t.setEmployeeId(5L);
        t.setEmployeeEmail("e@e.com");
        t.setRestaurantId(7L);

        TraceabilityRequestDto dto = mapper.toRequest(t);

        assertThat(dto).isNotNull();
        assertThat(dto.getOrderId()).isEqualTo(t.getOrderId());
        assertThat(dto.getClientEmail()).isEqualTo(t.getClientEmail());
        assertThat(dto.getNewStatus()).isEqualTo(t.getNewStatus());
        assertThat(dto.getEmployeeId()).isEqualTo(t.getEmployeeId());
    }

    @Test
    void toModel_shouldMapFields() {
        TraceabilityResponseDto r = new TraceabilityResponseDto();
        r.setId("abc");
        r.setOrderId(11L);
        r.setClientId(22L);
        r.setClientEmail("x@y.com");
        r.setDate(LocalDateTime.now());
        r.setPreviousStatus("PENDING");
        r.setNewStatus("READY");

        Traceability t = mapper.toModel(r);

        assertThat(t).isNotNull();
        assertThat(t.getId()).isEqualTo(r.getId());
        assertThat(t.getOrderId()).isEqualTo(r.getOrderId());
        assertThat(t.getNewStatus()).isEqualTo(r.getNewStatus());
        assertThat(t.getDate()).isEqualTo(r.getDate());
    }

    @Test
    void toModelList_shouldMapList() {
        TraceabilityResponseDto r1 = new TraceabilityResponseDto();
        r1.setId("1");
        TraceabilityResponseDto r2 = new TraceabilityResponseDto();
        r2.setId("2");

        List<Traceability> list = mapper.toModelList(List.of(r1, r2));

        assertThat(list).hasSize(2);
        assertThat(list.get(0).getId()).isEqualTo("1");
        assertThat(list.get(1).getId()).isEqualTo("2");
    }
}
