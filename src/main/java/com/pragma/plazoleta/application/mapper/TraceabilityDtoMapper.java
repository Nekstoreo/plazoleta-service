package com.pragma.plazoleta.application.mapper;

import com.pragma.plazoleta.application.dto.response.TraceabilityOrderItemDto;
import com.pragma.plazoleta.application.dto.response.TraceabilityResponseDto;
import com.pragma.plazoleta.domain.model.Traceability;
import com.pragma.plazoleta.domain.model.TraceabilityOrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface TraceabilityDtoMapper {
    TraceabilityResponseDto toResponse(Traceability traceability);
    List<TraceabilityResponseDto> toResponseList(List<Traceability> traceabilityList);
    TraceabilityOrderItemDto toOrderItemDto(TraceabilityOrderItem traceabilityOrderItem);
    List<TraceabilityOrderItemDto> toOrderItemDtoList(List<TraceabilityOrderItem> traceabilityOrderItems);
}
