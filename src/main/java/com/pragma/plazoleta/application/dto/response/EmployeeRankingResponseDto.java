package com.pragma.plazoleta.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeRankingResponseDto {
    private Long employeeId;
    private String employeeEmail;
    private Long restaurantId;
    private Long totalOrdersCompleted;
    private Double averageDurationInMinutes;
    private Integer rankingPosition;
}
