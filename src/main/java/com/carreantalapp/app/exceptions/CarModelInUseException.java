package com.carreantalapp.app.exceptions;

public class CarModelInUseException extends RuntimeException {
    public CarModelInUseException(Long carModelId) {
        super("Car model " + carModelId + " cannot be deleted — there are still cars of this model in the fleet.");
    }
}
