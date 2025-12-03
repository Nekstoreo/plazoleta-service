package com.pragma.plazoleta.infrastructure.output.feign.adapter;

import com.pragma.plazoleta.domain.spi.IClientInfoPort;
import com.pragma.plazoleta.infrastructure.output.feign.client.IUserFeignClient;
import com.pragma.plazoleta.infrastructure.output.feign.dto.UserDto;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ClientInfoFeignAdapter implements IClientInfoPort {

    private static final Logger logger = LoggerFactory.getLogger(ClientInfoFeignAdapter.class);

    private final IUserFeignClient userFeignClient;

    public ClientInfoFeignAdapter(IUserFeignClient userFeignClient) {
        this.userFeignClient = userFeignClient;
    }

    @Override
    public Optional<String> getClientPhoneById(Long clientId) {
        try {
            return userFeignClient.getUserById(clientId)
                    .map(UserDto::getPhone);
        } catch (FeignException e) {
            logger.error("Error fetching client phone for id {}: {}", clientId, e.getMessage(), e);
            return Optional.empty();
        }
    }

    @Override
    public Optional<String> getClientEmailById(Long clientId) {
        try {
            return userFeignClient.getUserById(clientId)
                    .map(UserDto::getEmail);
        } catch (FeignException e) {
            logger.error("Error fetching client email for id {}: {}", clientId, e.getMessage(), e);
            return Optional.empty();
        }
    }
}
