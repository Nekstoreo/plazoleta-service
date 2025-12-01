package com.pragma.plazoleta.domain.spi;

import java.util.Optional;

public interface IUserValidationPort {

    boolean existsById(Long userId);

    Optional<String> getUserRoleById(Long userId);
}
