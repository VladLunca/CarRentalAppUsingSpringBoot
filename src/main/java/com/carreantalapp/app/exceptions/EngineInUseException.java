package com.carreantalapp.app.exceptions;

public class EngineInUseException extends RuntimeException {
    public EngineInUseException(Long engineId) {
        super("Engine " + engineId + " cannot be deleted — it is still referenced by one or more car models.");
    }
}
