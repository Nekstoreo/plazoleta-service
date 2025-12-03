package com.pragma.plazoleta.infrastructure.output.feign.adapter;

import com.pragma.plazoleta.domain.spi.IEmployeeRestaurantPort;
import com.pragma.plazoleta.infrastructure.output.feign.client.IUserFeignClient;
import com.pragma.plazoleta.infrastructure.output.feign.dto.UserDto;
import feign.FeignException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class EmployeeRestaurantFeignAdapter implements IEmployeeRestaurantPort {

    private final IUserFeignClient userFeignClient;

    public EmployeeRestaurantFeignAdapter(IUserFeignClient userFeignClient) {
        this.userFeignClient = userFeignClient;
    }

    @Override
    public Optional<Long> getRestaurantIdByEmployeeId(Long employeeId) {
        try {
            return userFeignClient.getUserById(employeeId)
                    .map(UserDto::getRestaurantId);
        } catch (FeignException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<String> getEmployeeEmailById(Long employeeId) {
        try {
            return userFeignClient.getUserById(employeeId)
                    .map(UserDto::getEmail);
        } catch (FeignException e) {
            return Optional.empty();
        }
    }
}
