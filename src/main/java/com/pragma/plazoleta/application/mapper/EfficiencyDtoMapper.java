package com.pragma.plazoleta.application.mapper;

import com.pragma.plazoleta.application.dto.response.EmployeeRankingResponseDto;
import com.pragma.plazoleta.application.dto.response.OrderEfficiencyResponseDto;
import com.pragma.plazoleta.domain.model.EmployeeRanking;
import com.pragma.plazoleta.domain.model.OrderEfficiency;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface EfficiencyDtoMapper {
    
    OrderEfficiencyResponseDto toOrderEfficiencyResponse(OrderEfficiency orderEfficiency);
    List<OrderEfficiencyResponseDto> toOrderEfficiencyResponseList(List<OrderEfficiency> orderEfficiencyList);
    
    EmployeeRankingResponseDto toEmployeeRankingResponse(EmployeeRanking employeeRanking);
    List<EmployeeRankingResponseDto> toEmployeeRankingResponseList(List<EmployeeRanking> employeeRankingList);
}
