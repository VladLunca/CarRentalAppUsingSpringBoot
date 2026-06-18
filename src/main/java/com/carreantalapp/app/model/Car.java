package com.carreantalapp.app.model;

import com.carreantalapp.app.model.utils.CarStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "car")
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "car_id")
    private int id;

    @NotNull(message = "Car model must not be null")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "car_model_id", nullable = false)
    private CarModel carModel;

    @NotNull(message = "Company must not be null")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "car_rental_company_id", nullable = false)
    private CarRentalCompany carRentalCompany;

    @NotNull(message = "Licence plate must not be null")
    @Size(min = 7, max = 7, message = "Licence plate must be exactly 7 characters")
    @Column(name = "licence_plate", nullable = false, unique = true, length = 7)
    private String licencePlate;

    @NotNull(message = "Color must not be null")
    @Column(name = "color", nullable = false, length = 20)
    private String color;

    @Min(value = 0, message = "Mileage must not be negative")
    @Column(name = "mileage", nullable = false)
    private int mileage;

    @NotNull(message = "Status must not be null")
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 12)
    private CarStatus status = CarStatus.AVAILABLE;

    @Column(name = "image", length = 100)
    private String image;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public CarModel getCarModel() {
        return carModel;
    }

    public void setCarModel(CarModel carModel) {
        this.carModel = carModel;
    }

    public CarRentalCompany getCarRentalCompany() {
        return carRentalCompany;
    }

    public void setCarRentalCompany(CarRentalCompany carRentalCompany) {
        this.carRentalCompany = carRentalCompany;
    }

    public String getLicencePlate() {
        return licencePlate;
    }

    public void setLicencePlate(String licencePlate) {
        this.licencePlate = licencePlate;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getMileage() {
        return mileage;
    }

    public void setMileage(int mileage) {
        this.mileage = mileage;
    }

    public CarStatus getStatus() {
        return status;
    }

    public void setStatus(CarStatus status) {
        this.status = status;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
