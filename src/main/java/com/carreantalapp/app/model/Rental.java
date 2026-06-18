package com.carreantalapp.app.model;

import com.carreantalapp.app.model.utils.RentalStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

@Entity
@Table(name = "rental")
public class Rental {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rental_id")
    private int id;

    @NotNull(message = "Car must not be null")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "car_id", nullable = false)
    private Car car;

    @NotNull(message = "User must not be null")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @NotNull(message = "Pickup location must not be null")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pickup_location_id", nullable = false)
    private RentalLocation pickupLocation;

    @NotNull(message = "Dropoff location must not be null")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dropoff_location_id", nullable = false)
    private RentalLocation dropoffLocation;

    @NotNull(message = "Start date must not be null")
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @NotNull(message = "End date must not be null")
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @NotNull(message = "Status must not be null")
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 10)
    private RentalStatus status = RentalStatus.PENDING;

    @Column(name = "with_driver", nullable = false)
    private boolean withDriver = false;

    @Column(name = "child_seat", nullable = false)
    private boolean childSeat = false;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
    }

    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }

    public RentalLocation getPickupLocation() {
        return pickupLocation;
    }

    public void setPickupLocation(RentalLocation pickupLocation) {
        this.pickupLocation = pickupLocation;
    }

    public RentalLocation getDropoffLocation() {
        return dropoffLocation;
    }

    public void setDropoffLocation(RentalLocation dropoffLocation) {
        this.dropoffLocation = dropoffLocation;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public RentalStatus getStatus() {
        return status;
    }

    public void setStatus(RentalStatus status) {
        this.status = status;
    }

    public boolean isWithDriver() {
        return withDriver;
    }

    public void setWithDriver(boolean withDriver) {
        this.withDriver = withDriver;
    }

    public boolean isChildSeat() {
        return childSeat;
    }

    public void setChildSeat(boolean childSeat) {
        this.childSeat = childSeat;
    }
}
