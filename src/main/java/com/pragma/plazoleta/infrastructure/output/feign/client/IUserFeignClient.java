package com.pragma.plazoleta.infrastructure.output.feign.client;

import com.pragma.plazoleta.infrastructure.output.feign.dto.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Optional;

@FeignClient(name = "usuarios-service", url = "${microservices.usuarios.url}")
public interface IUserFeignClient {

    @GetMapping("/api/v1/users/{id}")
    Optional<UserDto> getUserById(@PathVariable("id") Long id);
}
