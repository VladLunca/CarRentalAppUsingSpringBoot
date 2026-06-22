package com.carreantalapp.app.repositories;

import com.carreantalapp.app.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {

    Optional<Address> findByCityNameAndStreetNameAndStreetNumber(String cityName, String streetName, int streetNumber);
}
