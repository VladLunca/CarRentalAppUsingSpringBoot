package com.carreantalapp.app.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CarModelDropdownDto {
    private final Long carModelId;
    private final String brand;
    private final String model;
    private final int year;
    private final int pricePerDay;
}
