package com.carreantalapp.app.repositories;

import com.carreantalapp.app.model.CarRentalCompany;
import com.carreantalapp.app.model.User;
import com.carreantalapp.app.model.utils.UserRoleTypes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    void deleteUserById(Long id);
    List<User> findByUsernameContainingIgnoreCase(String username);
    @Query("select u from User u join u.userRole r where r.carRentalCompany = :company and r.role = :role ")
    List<User> findByCarRentalCompanyAndRole(@Param("company") CarRentalCompany company, @Param("role") UserRoleTypes role);
}
