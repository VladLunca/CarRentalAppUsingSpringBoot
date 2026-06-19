package com.carreantalapp.app.model;

import com.carreantalapp.app.model.utils.UserRoleTypes;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "user_roles")
public class UserRoles {
    @Id
    @Column(name = "user_role_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Role must not be null")
    @Column(name = "role", nullable = false, length = 15)
    @Enumerated(EnumType.STRING)
    private UserRoleTypes role;

    @NotNull(message = "User must not be null")
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "car_rental_company_id")
    private CarRentalCompany carRentalCompany;

    public UserRoles(User user){
        role = UserRoleTypes.CUSTOMER;
        this.user = user;
    }

    public UserRoles() {
        role = UserRoleTypes.CUSTOMER;
    }
}
