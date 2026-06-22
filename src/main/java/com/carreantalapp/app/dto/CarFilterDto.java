package com.carreantalapp.app.dto;

import com.carreantalapp.app.model.utils.CarBodyTypes;
import com.carreantalapp.app.model.utils.TractionTypes;
import com.carreantalapp.app.model.utils.TransmissionTypes;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CarFilterDto {
    private List<String> brands;
    private List<CarBodyTypes> bodyTypes;
    private List<String> categories;
    private List<TransmissionTypes> transmissions;
    private List<TractionTypes> tractions;
    private Integer firstYear;
    private Integer lastYear;
}
