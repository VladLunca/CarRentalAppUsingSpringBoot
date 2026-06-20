package com.carreantalapp.app.repositories;

import com.carreantalapp.app.model.CarRentalCompany;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CarRentalCompanyRepository extends JpaRepository<CarRentalCompany, Long> {
    List<CarRentalCompany> findByNameContainingIgnoreCase(String searchTerm);
}
