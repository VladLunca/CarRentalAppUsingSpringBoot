package com.carreantalapp.app.exceptions;

public class TransmissionInUseException extends RuntimeException {
    public TransmissionInUseException(Long transmissionId) {
        super("Transmission " + transmissionId + " cannot be deleted — it is still referenced by one or more car models.");
    }
}
