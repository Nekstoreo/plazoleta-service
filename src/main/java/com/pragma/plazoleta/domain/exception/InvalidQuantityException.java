package com.pragma.plazoleta.domain.exception;

public class InvalidQuantityException extends RuntimeException {

    public InvalidQuantityException() {
        super("La cantidad de cada plato debe ser mayor a 0.");
    }
}
