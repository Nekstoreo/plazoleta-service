package com.pragma.plazoleta.infrastructure.output.feign.mapper;

import com.pragma.plazoleta.domain.model.Traceability;
import com.pragma.plazoleta.domain.model.TraceabilityOrderItem;
import com.pragma.plazoleta.infrastructure.output.feign.dto.TraceabilityRequestDto;
import com.pragma.plazoleta.infrastructure.output.feign.dto.TraceabilityResponseDto;
import com.pragma.plazoleta.application.dto.response.TraceabilityOrderItemDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface TraceabilityFeignMapper {
    TraceabilityRequestDto toRequest(Traceability traceability);
    Traceability toModel(TraceabilityResponseDto traceabilityResponseDto);
    List<Traceability> toModelList(List<TraceabilityResponseDto> traceabilityResponseDtos);
    TraceabilityOrderItemDto toOrderItemDto(TraceabilityOrderItem traceabilityOrderItem);
    List<TraceabilityOrderItemDto> toOrderItemDtoList(List<TraceabilityOrderItem> traceabilityOrderItems);
    TraceabilityOrderItem toOrderItemModel(TraceabilityOrderItemDto traceabilityOrderItemDto);
    List<TraceabilityOrderItem> toOrderItemModelList(List<TraceabilityOrderItemDto> traceabilityOrderItemDtos);
}
