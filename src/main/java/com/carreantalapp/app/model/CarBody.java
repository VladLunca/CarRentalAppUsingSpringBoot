package com.carreantalapp.app.model;

import com.carreantalapp.app.model.utils.CarBodyTypes;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "car_body")
public class CarBody {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="car_body_id")
    private Long id;

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

}
