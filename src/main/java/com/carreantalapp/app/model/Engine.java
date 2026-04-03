package com.carreantalapp.app.model;

import com.carreantalapp.app.model.utils.EngineTypes;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "engine")
public class Engine {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="engine_id")
    private Integer id;

    @NotNull
    @Column(name = "horse_power")
    private String horsePower;

    @NotNull
    @Column(name = "capacity")
    private int engineCapacity;

    @NotNull
    @Column(name = "engine_type")
    private EngineTypes engineType;

}
