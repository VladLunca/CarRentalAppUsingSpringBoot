package com.carreantalapp.app.repositories;

import com.carreantalapp.app.model.Car;
import com.carreantalapp.app.model.CarRentalCompany;
import com.carreantalapp.app.model.utils.CarStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CarRepository extends JpaRepository<Car, Long>, JpaSpecificationExecutor<Car> {

    List<Car> findByCarRentalCompany(CarRentalCompany company);

    List<Car> findByStatus(CarStatus status);

    @Query("select c from Car c where c.status = 'AVAILABLE' and c.id not in :rentedIds")
    List<Car> findAvailableExcluding(@Param("rentedIds") List<Long> rentedIds);

    @Query("select count(c) > 0 from Car c where c.carModel.carModelId = :carModelId")
    boolean existsByCarModelId(@Param("carModelId") Long carModelId);
}
