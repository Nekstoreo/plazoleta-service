package com.pragma.plazoleta.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeRanking {
    private Long employeeId;
    private String employeeEmail;
    private Long restaurantId;
    private Long totalOrdersCompleted;
    private Double averageDurationInMinutes;
    private Integer rankingPosition;
}
