package com.carreantalapp.app.dto;

import com.carreantalapp.app.model.utils.RentalStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class RentalDto {
    private final Long id;
    private final String carDescription;
    private final String username;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final String pickupLocation;
    private final String dropoffLocation;
    private final boolean withDriver;
    private final boolean childSeat;
    private final RentalStatus status;
    private final int totalPrice;
}
