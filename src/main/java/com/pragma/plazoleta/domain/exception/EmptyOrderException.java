package com.pragma.plazoleta.domain.exception;

public class EmptyOrderException extends RuntimeException {

    public EmptyOrderException() {
        super("El pedido debe contener al menos un plato.");
    }
}
