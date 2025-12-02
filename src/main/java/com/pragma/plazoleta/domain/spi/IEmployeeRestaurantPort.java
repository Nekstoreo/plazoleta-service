package com.pragma.plazoleta.domain.spi;

import java.util.Optional;

public interface IEmployeeRestaurantPort {

    Optional<Long> getRestaurantIdByEmployeeId(Long employeeId);
}
