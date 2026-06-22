package com.carreantalapp.app.exceptions;

public class RentalNotFoundException extends RuntimeException {
    public RentalNotFoundException(Long rentalId) {
        super("Rental not found with id: " + rentalId);
    }
}
