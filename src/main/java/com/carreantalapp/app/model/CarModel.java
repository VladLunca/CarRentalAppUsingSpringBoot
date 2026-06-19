package com.carreantalapp.app.model;

import com.carreantalapp.app.model.utils.TractionTypes;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name="car_model")
public class CarModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="car_model_id")
    private Long carModelId;

    @NotNull(message = "Brand must not be null")
    @Column(name = "brand")
    private String brand;

    @NotNull(message = "Model must not be null")
    @Column(name = "model")
    private String model;

    @Min(value = 1886, message = "Year must be 1886 or later")
    @Column(name= "year")
    private int year;

    @NotNull(message = "Engine must not be null")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "engine_id")
    private Engine engine;

    @NotNull(message = "Car body must not be null")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="car_body_id")
    private CarBody carBody;

    @NotNull(message = "Transmission must not be null")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="transmission_id")
    private Transmission transmission;

    @NotNull(message = "Category must not be null")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @NotNull(message = "Traction must not be null")
    @Column(name="traction")
    @Enumerated(EnumType.STRING)
    private TractionTypes traction;

    @Min(value = 0, message = "Fuel consumption must not be negative")
    @Column(name="fuel_consumption")
    private float fuelConsumption;

    @Min(value = 0, message = "Number of luggage must not be negative")
    @Column(name="number_of_luggage")
    private int numberOfLuggage;

    @Min(value = 1, message = "Price per day must be greater than 0")
    @Column(name="price_per_day")
    private int pricePerDay;
}
