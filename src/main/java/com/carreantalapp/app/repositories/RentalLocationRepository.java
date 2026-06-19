package com.carreantalapp.app.repositories;

import com.carreantalapp.app.model.RentalLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RentalLocationRepository extends JpaRepository<RentalLocation, Long> {
}
