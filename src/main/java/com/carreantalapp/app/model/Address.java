package com.carreantalapp.app.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name="address")
public class Address {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="address_id")
    private int id;

    @NotNull(message = "Street name must not be null")
    @Column(name="street_name")
    private String streetName;

    @NotNull(message = "City name must not be null")
    @Column(name="city_name")
    private String cityName;

    @Min(value = 1, message = "Street number must be greater than 0")
    @Column(name="street_number")
    private int streetNumber;

    public int getStreetNumber() {
        return streetNumber;
    }

    public void setStreetNumber(int streetNumber) {
        this.streetNumber = streetNumber;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStreetName() {
        return streetName;
    }

    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }
}
