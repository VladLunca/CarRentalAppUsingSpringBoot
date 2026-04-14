package com.carreantalapp.app.model;

import com.carreantalapp.app.model.utils.TransmissionTypes;
import jakarta.persistence.*;
import jakarta.validation.Constraint;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name="transmission")
public class Transmission {
    @Id
    @Column(name="transmission_id")
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private int id;

    @NotNull
    @Column(name = "transmission_type")
    @Enumerated(EnumType.STRING)
    private TransmissionTypes transmissionType;

    @NotNull
    @Column(name= "transmission_name")
    private String transmissionName;

    @NotNull
    @Min(1)
    @Column(name= "number_of_gears")
    private int numberOfGears;

}
