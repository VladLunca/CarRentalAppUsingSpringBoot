package com.carreantalapp.app.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "users")
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @NotNull(message = "Username must not be null")
    @Size(max = 20, message = "Username must not exceed 20 characters")
    @Column(name = "username", nullable = false, unique = true, length = 20)
    private String username;

    @NotNull(message = "Password must not be null")
    @Column(name = "password", nullable = false, length = 68)
    private String password;

    @NotNull(message = "User details must not be null")
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_details_id", nullable = false, unique = true)
    private UserDetails userDetails;

    @Column(name = "enabled", nullable = false)
    private boolean enabled = true;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserDetails getUserDetails() {
        return userDetails;
    }

    public void setUserDetails(UserDetails userDetails) {
        this.userDetails = userDetails;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
