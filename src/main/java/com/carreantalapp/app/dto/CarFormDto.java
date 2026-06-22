package com.carreantalapp.app.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CarFormDto {

    @NotNull(message = "Car model must be selected")
    private Long carModelId;

    @NotNull(message = "Licence plate must not be null")
    @Size(min = 7, max = 7, message = "Licence plate must be exactly 7 characters")
    private String licencePlate;

    @NotNull(message = "Color must not be null")
    private String color;

    @Min(value = 0, message = "Mileage must not be negative")
    private int mileage;
}
