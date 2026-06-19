package com.carreantalapp.app.repositories;

import com.carreantalapp.app.model.CarRentalCompany;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CarRentalCompanyRepository extends JpaRepository<CarRentalCompany, Long> {
}
