package com.pragma.plazoleta.infrastructure.output.feign.mapper;

import com.pragma.plazoleta.domain.model.EmployeeRanking;
import com.pragma.plazoleta.domain.model.OrderEfficiency;
import com.pragma.plazoleta.infrastructure.output.feign.dto.EmployeeRankingResponseDto;
import com.pragma.plazoleta.infrastructure.output.feign.dto.OrderEfficiencyResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface EfficiencyFeignMapper {
    
    OrderEfficiency toOrderEfficiency(OrderEfficiencyResponseDto responseDto);
    List<OrderEfficiency> toOrderEfficiencyList(List<OrderEfficiencyResponseDto> responseDtoList);
    
    EmployeeRanking toEmployeeRanking(EmployeeRankingResponseDto responseDto);
    List<EmployeeRanking> toEmployeeRankingList(List<EmployeeRankingResponseDto> responseDtoList);
}
