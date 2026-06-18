package com.carreantalapp.app.model;

import com.carreantalapp.app.model.utils.CarBodyTypes;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "car_body")
public class CarBody {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="car_body_id")
    private int id;

    @Min(value = 2, message = "Number of seats must be greater than 1")
    @Column(name="number_of_seats")
    private int numberOfSeats;

    @Min(value = 2, message = "Number of doors must be at least 2")
    @Column(name = "number_of_doors")
    private int numberOfDoors;

    @NotNull(message = "Car body type must not be null")
    @Column(name = "name")
    @Enumerated(EnumType.STRING)
    private CarBodyTypes name;

    public int getNumberOfDoors() {
        return numberOfDoors;
    }

    public int getNumberOfSeats() {
        return numberOfSeats;
    }

    public CarBodyTypes getName() {
        return name;
    }
}
