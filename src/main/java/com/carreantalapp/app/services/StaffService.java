package com.carreantalapp.app.services;

import com.carreantalapp.app.dto.UserDto;
import com.carreantalapp.app.exceptions.CompanyNotFoundException;
import com.carreantalapp.app.exceptions.CompanyRequiredException;
import com.carreantalapp.app.exceptions.InvalidRoleAssignmentException;
import com.carreantalapp.app.exceptions.UserNotFoundException;
import com.carreantalapp.app.model.CarRentalCompany;
import com.carreantalapp.app.model.User;
import com.carreantalapp.app.model.utils.UserRoleTypes;
import com.carreantalapp.app.repositories.CarRentalCompanyRepository;
import com.carreantalapp.app.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class StaffService {
    private final UserRepository userRepository;
    private final CarRentalCompanyRepository carRentalCompanyRepository;

    @Autowired
    public  StaffService(UserRepository userRepository, CarRentalCompanyRepository carRentalCompanyRepository) {
        this.userRepository = userRepository;
        this.carRentalCompanyRepository = carRentalCompanyRepository;
    }
    public List<UserDto> searchUsers(String searchTerm) {
        if (searchTerm == null || searchTerm.isBlank())
            return userRepository.findAll().stream().map(this::toDto).toList();
        return userRepository.findByUsernameContainingIgnoreCase(searchTerm)
                .stream().map(this::toDto).toList();
    }

    private UserDto toDto(User user) {
        return new UserDto(
                user.getId(),
                user.getUsername(),
                user.isEnabled(),
                user.getUserRole().getRole(),
                user.getUserRole().getCarRentalCompany() != null
                        ? user.getUserRole().getCarRentalCompany().getName() : null,
                user.getUserDetails().getFirstName(),
                user.getUserDetails().getLastName(),
                user.getUserDetails().getPhoneNumber(),
                user.getUserDetails().getCnp(),
                user.getUserDetails().getEmail()
        );
    }

    @Transactional
    public void removeRoleFromUserByUserId(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        user.getUserRole().setRole(UserRoleTypes.CUSTOMER);
        user.getUserRole().setCarRentalCompany(null);
    }

    private void validateRoleAssignment(UserRoleTypes assignerRole, UserRoleTypes roleToAssign) {
        if (roleToAssign.ordinal() >= assignerRole.ordinal()) {
            throw new InvalidRoleAssignmentException("Cannot assign role: " + roleToAssign);
        }
    }

    @Transactional
    public void addRoleToUserByUserId(Long userId, UserRoleTypes role, Long carRentalCompanyId, UserRoleTypes assignerRole) {
        validateRoleAssignment(assignerRole, role);
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        user.getUserRole().setRole(role);
        if(role == UserRoleTypes.EMPLOYEE ||  role == UserRoleTypes.MANAGER) {
            if (carRentalCompanyId == null) {
                throw new CompanyRequiredException(role.name());
            }
            user.getUserRole().setCarRentalCompany(carRentalCompanyRepository.findById(carRentalCompanyId)
                    .orElseThrow(() -> new CompanyNotFoundException(carRentalCompanyId)));
        }
        else {
            user.getUserRole().setCarRentalCompany(null);
        }
    }

    @Transactional(readOnly = true)
    public List<UserDto> getEmployeesOfCompany(Long carRentalCompanyId) {
        return userRepository.findByCarRentalCompanyAndRole(carRentalCompanyRepository.findById(carRentalCompanyId)
                        .orElseThrow(() -> new CompanyNotFoundException(carRentalCompanyId)), UserRoleTypes.EMPLOYEE)
                .stream().map(this::toDto).toList();
    }

    @Transactional
    public void toggleAccountStatus(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        user.setEnabled(!user.isEnabled());
    }

    public UserRoleTypes getRoleFromAuthentication(Authentication auth) {
        String authority = auth.getAuthorities().iterator().next().getAuthority();
        if (authority == null) throw new InvalidRoleAssignmentException("No role found for current user");
        return UserRoleTypes.valueOf(authority.replace("ROLE_", ""));
    }

    public Long getCarRentalCompanyIdByUserId(Authentication  auth ) {
        User u = userRepository.findByUsername(auth.getName()).orElseThrow(() -> new UserNotFoundException(auth.getName()));
        if(u.getUserRole().getRole().equals(UserRoleTypes.CUSTOMER) || u.getUserRole().getRole().equals(UserRoleTypes.SUPER_ADMIN)) {
            return null;
        }
        return  u.getUserRole().getCarRentalCompany().getId();
    }
    public String getCompanyNameById(Long companyId) {
        return carRentalCompanyRepository.findById(companyId)
                .orElseThrow(() -> new CompanyNotFoundException(companyId))
                .getName();
    }
    @Transactional(readOnly = true)
    public List<UserDto> searchUsersForCompany(String searchTerm, Long companyId) {
        if (searchTerm == null || searchTerm.isBlank())
            return List.of();
        CarRentalCompany company = carRentalCompanyRepository.findById(companyId)
                .orElseThrow(() -> new CompanyNotFoundException(companyId));
        return userRepository.findSearchableUsersForCompany(searchTerm, company)
                .stream()
                .map(this::toDto)
                .toList();
    }


}
