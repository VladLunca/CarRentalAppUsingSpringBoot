package com.carreantalapp.app.repositories;

import com.carreantalapp.app.model.Address;
import com.carreantalapp.app.model.CarRentalCompany;
import com.carreantalapp.app.model.RentalLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RentalLocationRepository extends JpaRepository<RentalLocation, Long> {

    Optional<RentalLocation> findByAddressAndCarRentalCompany(Address address, CarRentalCompany company);
}
