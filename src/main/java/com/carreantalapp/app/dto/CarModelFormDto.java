package com.carreantalapp.app.dto;

import com.carreantalapp.app.model.utils.TractionTypes;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CarModelFormDto {

    @NotNull
    private String brand;

    @NotNull
    private String model;

    @Min(1886)
    private int year;

    @Min(1)
    private int pricePerDay;

    @NotNull
    private Long engineId;

    @NotNull
    private Long transmissionId;

    @NotNull
    private Long carBodyId;

    @NotNull
    private Long categoryId;

    @NotNull
    private TractionTypes traction;

    @Min(0)
    private float fuelConsumption;

    @Min(0)
    private int numberOfLuggage;
}
