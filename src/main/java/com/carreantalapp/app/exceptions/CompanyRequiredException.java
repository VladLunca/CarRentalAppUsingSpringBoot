package com.carreantalapp.app.exceptions;

public class CompanyRequiredException extends RuntimeException {
    public CompanyRequiredException(String role) {
        super("A company must be assigned for role: " + role);
    }
}
