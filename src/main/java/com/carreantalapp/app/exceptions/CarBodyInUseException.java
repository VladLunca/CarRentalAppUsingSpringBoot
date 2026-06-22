package com.carreantalapp.app.exceptions;

public class CarBodyInUseException extends RuntimeException {
    public CarBodyInUseException(Long carBodyId) {
        super("Car body " + carBodyId + " cannot be deleted — it is still referenced by one or more car models.");
    }
}
