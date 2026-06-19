package com.carreantalapp.app.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "users")
public class User {
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
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_details_id", nullable = false, unique = true)
    private UserDetails userDetails;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private UserRoles userRole ;

    @Column(name = "enabled", nullable = false)
    private boolean enabled = true;

    public User(String username, String password, UserDetails userDetails) {
        this.username = username;
        this.password = password;
        this.userDetails = userDetails;
        this.userRole = new UserRoles(this);
    }
}
