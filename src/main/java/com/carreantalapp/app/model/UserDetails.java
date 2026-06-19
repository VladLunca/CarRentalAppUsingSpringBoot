package com.carreantalapp.app.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "user_details")
public class UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_details_id")
    private Long id;

    @NotNull(message = "First name must not be null")
    @Column(name = "first_name", nullable = false, length = 68)
    private String firstName;

    @NotNull(message = "Last name must not be null")
    @Column(name = "last_name", nullable = false, length = 68)
    private String lastName;

    @NotNull(message = "Email must not be null")
    @Pattern(regexp = "^[\\w.+-]+@(gmail|yahoo|outlook)\\.(com|ro|net|org|ca)$", message = "Email must be a valid address with a known domain (e.g. name@gmail.com)")
    @Column(name = "email", nullable = false, unique = true, length = 68)
    private String email;

    @NotNull(message = "CNP must not be null")
    @Pattern(regexp = "\\d{13}", message = "CNP must contain exactly 13 digits")
    @Column(name = "cnp", nullable = false, unique = true, length = 13)
    private String cnp;

    @NotNull(message = "Phone number must not be null")
    @Pattern(regexp = "\\d{10}", message = "Phone number must contain exactly 10 digits")
    @Column(name = "phone_number", nullable = false, length = 10)
    private String phoneNumber;

    public UserDetails(String firstName, String lastName, String email, String cnp, String phoneNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.cnp = cnp;
        this.phoneNumber = phoneNumber;
    }
}
