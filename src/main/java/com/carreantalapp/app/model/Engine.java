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
    @Enumerated(EnumType.STRING)
    private EngineTypes engineType;

    public String getHorsePower() {
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

    public void setHorsePower(String horsePower) {
        this.horsePower = horsePower;
    }
}
