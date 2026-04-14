package com.carreantalapp.app.model;


import com.carreantalapp.app.model.utils.CarBodyTypes;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "car_body")
public class CarBody {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="car_body_id")
    private int id;

    @NotNull
    @Column(name="number_of_seats")
    private int numberOfSeats;

    @NotNull
    @Column(name = "number_of_doors")
    private int numberOfDoors;

    @NotNull
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
