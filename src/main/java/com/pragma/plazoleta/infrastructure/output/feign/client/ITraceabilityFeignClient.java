package com.pragma.plazoleta.infrastructure.output.feign.client;

import com.pragma.plazoleta.infrastructure.output.feign.dto.EmployeeRankingResponseDto;
import com.pragma.plazoleta.infrastructure.output.feign.dto.OrderEfficiencyResponseDto;
import com.pragma.plazoleta.infrastructure.output.feign.dto.TraceabilityRequestDto;
import com.pragma.plazoleta.infrastructure.output.feign.dto.TraceabilityResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "trazabilidad-service", url = "${microservices.trazabilidad.url}")
public interface ITraceabilityFeignClient {

    @PostMapping("/traceability")
    void saveTraceability(@RequestBody TraceabilityRequestDto traceabilityRequestDto);

    @GetMapping("/traceability/{orderId}")
    List<TraceabilityResponseDto> getTraceabilityByOrderId(@PathVariable("orderId") Long orderId);

    @GetMapping("/traceability/efficiency/restaurant/{restaurantId}/orders")
    List<OrderEfficiencyResponseDto> getOrdersEfficiencyByRestaurant(@PathVariable("restaurantId") Long restaurantId);

    @GetMapping("/traceability/efficiency/restaurant/{restaurantId}/employees")
    List<EmployeeRankingResponseDto> getEmployeeRankingByRestaurant(@PathVariable("restaurantId") Long restaurantId);
}
