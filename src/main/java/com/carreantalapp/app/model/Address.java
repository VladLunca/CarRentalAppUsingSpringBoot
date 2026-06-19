package com.carreantalapp.app.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name="address")
public class Address {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="address_id")
    private Long id;

    @NotNull(message = "Street name must not be null")
    @Column(name="street_name")
    private String streetName;

    @NotNull(message = "City name must not be null")
    @Column(name="city_name")
    private String cityName;

    @Min(value = 1, message = "Street number must be greater than 0")
    @Column(name="street_number")
    private int streetNumber;

}
