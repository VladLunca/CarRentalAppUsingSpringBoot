package com.carreantalapp.app.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

@Entity
@Table(name = "rental_location")
public class RentalLocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rental_location_id")
    private int id;

    @NotNull(message = "Company must not be null")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "car_rental_company_id", nullable = false)
    private CarRentalCompany carRentalCompany;

    @NotNull(message = "Address must not be null")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "address_id", nullable = false)
    private Address address;

    @NotNull(message = "Name must not be null")
    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Pattern(regexp = "\\d{10}", message = "Phone number must contain exactly 10 digits")
    @Column(name = "phone_number", length = 10)
    private String phoneNumber;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public CarRentalCompany getCarRentalCompany() {
        return carRentalCompany;
    }

    public void setCarRentalCompany(CarRentalCompany carRentalCompany) {
        this.carRentalCompany = carRentalCompany;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
