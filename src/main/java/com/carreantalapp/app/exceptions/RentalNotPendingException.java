package com.carreantalapp.app.exceptions;

public class RentalNotPendingException extends RuntimeException {
    public RentalNotPendingException(String action) {
        super("Cannot " + action + " a rental that is not PENDING.");
    }
}
