package com.pragma.plazoleta.infrastructure.output.feign.adapter;

import com.pragma.plazoleta.domain.spi.IUserValidationPort;
import com.pragma.plazoleta.infrastructure.output.feign.client.IUserFeignClient;
import com.pragma.plazoleta.infrastructure.output.feign.dto.UserDto;
import feign.FeignException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserFeignAdapter implements IUserValidationPort {

    private final IUserFeignClient userFeignClient;

    public UserFeignAdapter(IUserFeignClient userFeignClient) {
        this.userFeignClient = userFeignClient;
    }

    @Override
    public boolean existsById(Long userId) {
        try {
            return userFeignClient.getUserById(userId).isPresent();
        } catch (FeignException e) {
            return false;
        }
    }

    @Override
    public Optional<String> getUserRoleById(Long userId) {
        try {
            return userFeignClient.getUserById(userId)
                    .map(UserDto::getRole);
        } catch (FeignException e) {
            return Optional.empty();
        }
    }
}
