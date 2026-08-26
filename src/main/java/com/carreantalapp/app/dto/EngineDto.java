package com.carreantalapp.app.dto;

import com.carreantalapp.app.model.utils.EngineTypes;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class EngineDto {

    private Long engineId;

    @Min(1)
    private int horsePower;

    @Min(0)
    private float engineCapacity;

    @NotNull
    private EngineTypes engineType;

    @Override
    public String toString() {
        return horsePower + "hp " + engineType + " (" + engineCapacity + "cc)";
    }
}
