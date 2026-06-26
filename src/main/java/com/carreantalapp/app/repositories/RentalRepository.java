package com.carreantalapp.app.repositories;

import com.carreantalapp.app.model.Car;
import com.carreantalapp.app.model.Rental;
import com.carreantalapp.app.model.User;
import com.carreantalapp.app.model.utils.RentalStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RentalRepository extends JpaRepository<Rental, Long> {

    @Query("select r.car.id from Rental r where r.startDate <= :end and r.endDate >= :start and r.status != 'CANCELLED'")
    List<Long> findRentedCarIdsBetween(@Param("start") LocalDate start, @Param("end") LocalDate end);

    @Query("select count(r) > 0 from Rental r where r.car.id = :carId and r.startDate <= :end and r.endDate >= :start and r.status != 'CANCELLED'")
    boolean existsOverlappingRental(@Param("carId") Long carId, @Param("start") LocalDate start, @Param("end") LocalDate end);

    boolean existsByCarAndStatusIn(Car car, List<RentalStatus> statuses);

    @Query("select r from Rental r where r.car.id = :carId and r.status = 'PENDING' and r.startDate <= :cutoff")
    List<Rental> findPendingRentalsForCarBefore(@Param("carId") Long carId, @Param("cutoff") LocalDate cutoff);

    List<Rental> findByUser(User user);

    List<Rental> findByCar_CarRentalCompany_Id(Long companyId);

    List<Rental> findByStatusAndEndDateBefore(RentalStatus status, LocalDate date);
}
