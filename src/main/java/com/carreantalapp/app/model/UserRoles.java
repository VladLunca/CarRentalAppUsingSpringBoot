package com.carreantalapp.app.model;

import com.carreantalapp.app.model.utils.UserRoleTypes;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "user_roles")
public class UserRoles {
    @Id
    @Column(name = "user_role_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotNull(message = "Role must not be null")
    @Column(name = "role", nullable = false, length = 15)
    @Enumerated(EnumType.STRING)
    private UserRoleTypes role;

    @NotNull(message = "User must not be null")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "car_rental_company_id")
    private CarRentalCompany carRentalCompany;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public UserRoleTypes getRole() {
        return role;
    }

    public void setRole(UserRoleTypes role) {
        this.role = role;
    }

    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }

    public CarRentalCompany getCarRentalCompany() {
        return carRentalCompany;
    }

    public void setCarRentalCompany(CarRentalCompany carRentalCompany) {
        this.carRentalCompany = carRentalCompany;
    }
}
