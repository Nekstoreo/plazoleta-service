package com.pragma.plazoleta.domain.exception;

public class EmployeeNotAssociatedWithRestaurantException extends RuntimeException {

    public EmployeeNotAssociatedWithRestaurantException(Long employeeId) {
        super("Employee with id " + employeeId + " is not associated with any restaurant");
    }
}
