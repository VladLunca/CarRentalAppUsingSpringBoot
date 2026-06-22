package com.carreantalapp.app.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class RentalFormDto {

    @NotNull
    private Long carId;

    @NotNull
    private LocalDate startDate;

    @NotNull
    private LocalDate endDate;

    private boolean withDriver;
    private boolean childSeat;

    @NotBlank
    private String pickupCity;

    @NotBlank
    private String pickupStreet;

    @Min(1)
    private int pickupStreetNumber;

    @NotBlank
    private String dropoffCity;

    @NotBlank
    private String dropoffStreet;

    @Min(1)
    private int dropoffStreetNumber;
}
