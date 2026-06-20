package com.carreantalapp.app.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CarRentalCompanyDto {
    private Long id;

    @NotBlank(message = "Company name is required")
    private String name;

    @NotBlank(message = "Email is required")
    @Pattern(regexp = "^[\\w.+-]+@(gmail|yahoo|outlook)\\.(com|ro|net|org|ca)$",
             message = "Email must be a valid address (e.g. name@gmail.com)")
    private String email;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "\\d{10}", message = "Phone number must contain exactly 10 digits")
    private String phoneNumber;

    private String description;

    @NotBlank(message = "City is required")
    private String cityName;

    @NotBlank(message = "Street is required")
    private String streetName;

    @Min(value = 1, message = "Street number must be greater than 0")
    private int streetNumber;
}
