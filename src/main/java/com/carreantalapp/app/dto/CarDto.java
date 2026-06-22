package com.carreantalapp.app.dto;

import com.carreantalapp.app.model.utils.CarStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CarDto {
    private final Long id;
    private final String brand;
    private final String model;
    private final int year;
    private final int numberOfSeats;
    private final String licencePlate;
    private final String color;
    private final int mileage;
    private final int horsePower;
    private final String engineType;
    private final String transmissionName;
    private final String traction;
    private final String categoryName;
    private final int pricePerDay;
    private final CarStatus status;
    private final String image;
}
