package com.carreantalapp.app.repositories;

import com.carreantalapp.app.model.CarBody;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CarBodyRepository extends JpaRepository<CarBody, Long> {
}
