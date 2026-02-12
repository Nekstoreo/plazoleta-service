package com.pragma.plazoleta.domain.exception;

public class EmptyOrderException extends RuntimeException {

    public EmptyOrderException() {
        super("The order must contain at least one dish.");
    }
}
