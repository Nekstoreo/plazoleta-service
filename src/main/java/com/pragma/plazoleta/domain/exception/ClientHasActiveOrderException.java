package com.pragma.plazoleta.domain.exception;

public class ClientHasActiveOrderException extends RuntimeException {

    public ClientHasActiveOrderException(Long clientId) {
        super("Client with ID " + clientId + " already has an active order. " +
              "Please wait until the current order is delivered or canceled.");
    }
}
