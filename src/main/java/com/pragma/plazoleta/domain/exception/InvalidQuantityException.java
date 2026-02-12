package com.pragma.plazoleta.domain.exception;

public class InvalidQuantityException extends RuntimeException {

    public InvalidQuantityException() {
        super("The quantity of each dish must be greater than 0.");
    }
}
