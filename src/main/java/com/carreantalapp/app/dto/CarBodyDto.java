package com.carreantalapp.app.dto;

import com.carreantalapp.app.model.utils.CarBodyTypes;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CarBodyDto {

    private Long id;

    @NotNull
    private CarBodyTypes name;

    @Min(2)
    private int numberOfSeats;

    @Min(2)
    private int numberOfDoors;
}
