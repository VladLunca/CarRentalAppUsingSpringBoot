package com.carreantalapp.app.exceptions;

public class CategoryInUseException extends RuntimeException {
    public CategoryInUseException(Long categoryId) {
        super("Category " + categoryId + " cannot be deleted — it is still referenced by one or more car models.");
    }
}
