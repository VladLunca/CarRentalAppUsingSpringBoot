package com.carreantalapp.app.model;

import com.carreantalapp.app.model.utils.TransmissionTypes;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name="transmission")
public class Transmission {
    @Id
    @Column(name="transmission_id")
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Transmission type must not be null")
    @Column(name = "transmission_type")
    @Enumerated(EnumType.STRING)
    private TransmissionTypes transmissionType;

    @NotNull(message = "Transmission name must not be null")
    @Column(name= "transmission_name")
    private String transmissionName;

    @Min(value = 1, message = "Number of gears must be greater than 0")
    @Column(name= "number_of_gears")
    private int numberOfGears;
}
