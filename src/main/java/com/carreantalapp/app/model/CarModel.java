package com.carreantalapp.app.model;

import com.carreantalapp.app.model.utils.TractionTypes;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name="car_model")
public class CarModel {
    @Id
    @Column(name="car_model_id")
    private int carModelId;

    @NotNull
    @Column(name = "brand")
    private String brand;

    @NotNull
    @Column(name = "model")
    private String model;

    @NotNull
    @Column(name= "year")
    private int year;


    @ManyToOne
    @JoinColumn(name = "engine_id")
    private Engine engine;

    @ManyToOne
    @JoinColumn(name="car_body_id")
    private CarBody carBody;

    @ManyToOne
    @JoinColumn(name="transmission_id")
    private Transmission transmission;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(name="traction")
    @Enumerated(EnumType.STRING)
    private TractionTypes traction;

    @Column(name="fuel_consumption")
    @Min(0)
    private float fuelConsumption;

    @Column(name="number_of_luggage")
    @Min(0)
    private int numberOfLuggage;

    @Column(name="price_per_day")
    @Min(0)
    private int pricePerDay;



}
