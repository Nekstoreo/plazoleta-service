package com.pragma.plazoleta.domain.exception;

public class ClientPhoneNotFoundException extends RuntimeException {

    public ClientPhoneNotFoundException(Long clientId) {
        super(String.format("Phone number not found for client with id %d", clientId));
    }
}
