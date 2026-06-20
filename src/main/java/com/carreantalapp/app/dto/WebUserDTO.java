package com.carreantalapp.app.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WebUserDTO {

    @NotBlank(message = "Username is required")
    @Size(max = 20, message = "Username must not exceed 20 characters")
    private String userName;

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    @NotBlank(message = "Please confirm your password")
    private String confirmPassword;

    @NotBlank(message = "CNP is required")
    @Pattern(regexp = "\\d{13}", message = "CNP must contain exactly 13 digits")
    private String cnp;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "\\d{10}", message = "Phone number must contain exactly 10 digits")
    private String phoneNumber;

    @NotBlank(message = "Email is required")
    @Pattern(regexp = "^[\\w.+-]+@(gmail|yahoo|outlook)\\.(com|ro|net|org|ca)$", message = "Email must be a valid address (e.g. name@gmail.com)")
    private String email;
}

