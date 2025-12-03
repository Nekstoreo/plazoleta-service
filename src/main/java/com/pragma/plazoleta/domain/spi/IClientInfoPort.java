package com.pragma.plazoleta.domain.spi;

import java.util.Optional;

public interface IClientInfoPort {

    Optional<String> getClientPhoneById(Long clientId);
}
