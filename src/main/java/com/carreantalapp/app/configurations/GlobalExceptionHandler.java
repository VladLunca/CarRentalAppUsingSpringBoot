package com.carreantalapp.app.configurations;

import com.carreantalapp.app.exceptions.CompanyNotFoundException;
import com.carreantalapp.app.exceptions.RentalNotFoundException;
import com.carreantalapp.app.exceptions.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({UserNotFoundException.class, CompanyNotFoundException.class, RentalNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNotFound(RuntimeException e, Model model) {
        model.addAttribute("status", 404);
        model.addAttribute("message", e.getMessage());
        return "error";
    }

    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String handleForbidden(IllegalStateException e, Model model) {
        model.addAttribute("status", 403);
        model.addAttribute("message", e.getMessage());
        return "error";
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleGeneral(Exception e, Model model) {
        model.addAttribute("status", 500);
        model.addAttribute("message", "An unexpected error occurred.");
        return "error";
    }
}
