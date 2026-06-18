package com.carreantalapp.app.model;

import com.carreantalapp.app.model.utils.EngineTypes;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "engine")
public class Engine {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="engine_id")
    private Integer id;

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

    public int getHorsePower() {
        return horsePower;
    }

    public int getEngineCapacity() {
        return engineCapacity;
    }

    public EngineTypes getEngineType() {
        return engineType;
    }

    public void setEngineCapacity(int engineCapacity) {
        this.engineCapacity = engineCapacity;
    }

    public void setEngineType(EngineTypes engineType) {
        this.engineType = engineType;
    }

    public void setHorsePower(int horsePower) {
        this.horsePower = horsePower;
    }
}
