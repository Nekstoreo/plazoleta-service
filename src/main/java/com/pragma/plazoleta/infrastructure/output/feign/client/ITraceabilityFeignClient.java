package com.pragma.plazoleta.infrastructure.output.feign.client;

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
}
