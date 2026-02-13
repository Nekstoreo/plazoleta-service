package com.pragma.plazoleta.infrastructure.output.feign.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

import com.pragma.plazoleta.application.dto.response.TraceabilityOrderItemDto;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TraceabilityResponseDto {
    private String id;
    private Long orderId;
    private Long clientId;
    private String clientEmail;
    private LocalDateTime date;
    private String previousStatus;
    private String newStatus;
    private Long employeeId;
    private String employeeEmail;
    private Long restaurantId;
    private List<TraceabilityOrderItemDto> orderItems;
    private Long totalOrderAmount;
}
