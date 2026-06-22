package com.carreantalapp.app.repositories;

import com.carreantalapp.app.model.CarBody;
import com.carreantalapp.app.model.utils.CarBodyTypes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CarBodyRepository extends JpaRepository<CarBody, Long> {

    Optional<CarBody> findByNameAndNumberOfSeatsAndNumberOfDoors(CarBodyTypes name, int numberOfSeats, int numberOfDoors);
}
