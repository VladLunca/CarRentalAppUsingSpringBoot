package com.carreantalapp.app.dto;

import com.carreantalapp.app.model.utils.TransmissionTypes;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TransmissionDto {

    private Long transmissionId;

    @NotNull
    private String transmissionName;

    @NotNull
    private TransmissionTypes transmissionType;

    @Min(1)
    private int numberOfGears;
}
