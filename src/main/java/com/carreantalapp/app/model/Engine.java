package com.carreantalapp.app.model;

import com.carreantalapp.app.model.utils.EngineTypes;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "engine")
public class Engine {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="engine_id")
    private Long id;

    @Min(value = 1, message = "Horse power must be greater than 0")
    @Column(name = "horse_power")
    private int horsePower;

    @Min(value = 0, message = "Engine capacity must not be less than 0")
    @Column(name = "capacity")
    private int engineCapacity;

    @NotNull(message = "Engine type must not be null")
    @Column(name = "engine_type")
    @Enumerated(EnumType.STRING)
    private EngineTypes engineType;
}
