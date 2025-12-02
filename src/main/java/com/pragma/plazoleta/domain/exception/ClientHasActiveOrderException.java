package com.pragma.plazoleta.domain.exception;

public class ClientHasActiveOrderException extends RuntimeException {

    public ClientHasActiveOrderException(Long clientId) {
        super("El cliente con ID " + clientId + " ya tiene un pedido en proceso. " +
              "Debe esperar a que el pedido actual sea entregado o cancelado.");
    }
}
