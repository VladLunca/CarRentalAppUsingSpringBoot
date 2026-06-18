package com.carreantalapp.app.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

@Entity
@Table(name="car_rental_company")
public class CarRentalCompany {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="car_rental_company_id")
    private Long id;

    @NotNull(message = "Name must not be null")
    @Column(name="car_rental_company_name")
    private String name;

    @NotNull(message = "Address must not be null")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "address_id")
    private Address address;

    @NotNull(message = "Phone number must not be null")
    @Pattern(regexp = "\\d{10}", message = "Phone number must contain exactly 10 digits")
    @Column(name = "phone_number", length = 10)
    private String phoneNumber;

    @NotNull(message = "Email must not be null")
    @Pattern(regexp = "^[\\w.+-]+@(gmail|yahoo|outlook)\\.(com|ro|net|org|ca)$", message = "Email must be a valid address with a known domain (e.g. name@gmail.com)")
    @Column(name = "email", unique = true)
    private String email;

    @Column(name="description")
    private String description;


    public void setId(Long id) {
        this.id = id;
    }


    public Long getId() {
        return id;
    }
}
