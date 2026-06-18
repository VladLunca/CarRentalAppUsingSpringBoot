package com.carreantalapp.app.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

@Entity
@Table(name = "user_details")
public class UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_details_id")
    private int id;

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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCnp() {
        return cnp;
    }

    public void setCnp(String cnp) {
        this.cnp = cnp;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
