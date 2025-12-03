package com.pragma.plazoleta.infrastructure.output.feign.mapper;

import com.pragma.plazoleta.domain.model.Traceability;
import com.pragma.plazoleta.infrastructure.output.feign.dto.TraceabilityRequestDto;
import com.pragma.plazoleta.infrastructure.output.feign.dto.TraceabilityResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface TraceabilityFeignMapper {
    TraceabilityRequestDto toRequest(Traceability traceability);
    Traceability toModel(TraceabilityResponseDto traceabilityResponseDto);
    List<Traceability> toModelList(List<TraceabilityResponseDto> traceabilityResponseDtos);
}
