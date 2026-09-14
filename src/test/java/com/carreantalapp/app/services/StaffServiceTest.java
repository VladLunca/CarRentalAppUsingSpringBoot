package com.carreantalapp.app.services;

import com.carreantalapp.app.dto.UserDto;
import com.carreantalapp.app.exceptions.CompanyNotFoundException;
import com.carreantalapp.app.exceptions.CompanyRequiredException;
import com.carreantalapp.app.exceptions.InvalidRoleAssignmentException;
import com.carreantalapp.app.exceptions.UserNotFoundException;
import com.carreantalapp.app.model.CarRentalCompany;
import com.carreantalapp.app.model.User;
import com.carreantalapp.app.model.UserDetails;
import com.carreantalapp.app.model.utils.UserRoleTypes;
import com.carreantalapp.app.repositories.CarRentalCompanyRepository;
import com.carreantalapp.app.repositories.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StaffServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private CarRentalCompanyRepository carRentalCompanyRepository;

    @InjectMocks
    private StaffService staffService;

    private CarRentalCompany company(Long id, String name) {
        CarRentalCompany company = new CarRentalCompany();
        company.setId(id);
        company.setName(name);
        return company;
    }

    private User user(Long id, String username, UserRoleTypes role, CarRentalCompany company) {
        User user = new User(
                username,
                "{noop}secret",
                new UserDetails("Ana", "Pop", "ana@gmail.com", "1234567890123", "0712345678"));
        user.setId(id);
        user.getUserRole().setRole(role);
        user.getUserRole().setCarRentalCompany(company);
        return user;
    }

    private Authentication auth(String username, String... authorities) {
        return new UsernamePasswordAuthenticationToken(
                username,
                "secret",
                List.of(authorities).stream().map(SimpleGrantedAuthority::new).toList());
    }

    @Nested
    @DisplayName("addRoleToUserByUserId")
    class AddRole {

        @Test
        void managerCannotAssignManagerRole() {
            assertThatThrownBy(() -> staffService.addRoleToUserByUserId(
                    7L, UserRoleTypes.MANAGER, 1L, UserRoleTypes.MANAGER))
                    .isInstanceOf(InvalidRoleAssignmentException.class)
                    .hasMessageContaining("MANAGER");
            verify(userRepository, never()).findById(any());
        }

        @Test
        void managerCannotAssignSuperAdminRole() {
            assertThatThrownBy(() -> staffService.addRoleToUserByUserId(
                    7L, UserRoleTypes.SUPER_ADMIN, 1L, UserRoleTypes.MANAGER))
                    .isInstanceOf(InvalidRoleAssignmentException.class);
            verify(userRepository, never()).findById(any());
        }

        @Test
        void managerCanAssignEmployeeRole() {
            CarRentalCompany company = company(1L, "AutoRent");
            User target = user(7L, "dan", UserRoleTypes.CUSTOMER, null);
            when(userRepository.findById(7L)).thenReturn(Optional.of(target));
            when(carRentalCompanyRepository.findById(1L)).thenReturn(Optional.of(company));

            staffService.addRoleToUserByUserId(7L, UserRoleTypes.EMPLOYEE, 1L, UserRoleTypes.MANAGER);

            assertThat(target.getUserRole().getRole()).isEqualTo(UserRoleTypes.EMPLOYEE);
            assertThat(target.getUserRole().getCarRentalCompany()).isSameAs(company);
        }

        @Test
        void superAdminCanAssignManagerRole() {
            CarRentalCompany company = company(1L, "AutoRent");
            User target = user(7L, "dan", UserRoleTypes.CUSTOMER, null);
            when(userRepository.findById(7L)).thenReturn(Optional.of(target));
            when(carRentalCompanyRepository.findById(1L)).thenReturn(Optional.of(company));

            staffService.addRoleToUserByUserId(7L, UserRoleTypes.MANAGER, 1L, UserRoleTypes.SUPER_ADMIN);

            assertThat(target.getUserRole().getRole()).isEqualTo(UserRoleTypes.MANAGER);
        }

        @Test
        void staffRoleRequiresCompany() {
            User target = user(7L, "dan", UserRoleTypes.CUSTOMER, null);
            when(userRepository.findById(7L)).thenReturn(Optional.of(target));

            assertThatThrownBy(() -> staffService.addRoleToUserByUserId(
                    7L, UserRoleTypes.EMPLOYEE, null, UserRoleTypes.MANAGER))
                    .isInstanceOf(CompanyRequiredException.class)
                    .hasMessageContaining("EMPLOYEE");
        }

        @Test
        void rejectsUnknownCompany() {
            User target = user(7L, "dan", UserRoleTypes.CUSTOMER, null);
            when(userRepository.findById(7L)).thenReturn(Optional.of(target));
            when(carRentalCompanyRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> staffService.addRoleToUserByUserId(
                    7L, UserRoleTypes.EMPLOYEE, 99L, UserRoleTypes.MANAGER))
                    .isInstanceOf(CompanyNotFoundException.class);
        }

        @Test
        void rejectsUnknownUser() {
            when(userRepository.findById(7L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> staffService.addRoleToUserByUserId(
                    7L, UserRoleTypes.EMPLOYEE, 1L, UserRoleTypes.MANAGER))
                    .isInstanceOf(UserNotFoundException.class);
        }

        @Test
        void demotingToCustomerClearsCompany() {
            CarRentalCompany company = company(1L, "AutoRent");
            User target = user(7L, "dan", UserRoleTypes.EMPLOYEE, company);
            when(userRepository.findById(7L)).thenReturn(Optional.of(target));

            staffService.addRoleToUserByUserId(7L, UserRoleTypes.CUSTOMER, 1L, UserRoleTypes.MANAGER);

            assertThat(target.getUserRole().getRole()).isEqualTo(UserRoleTypes.CUSTOMER);
            assertThat(target.getUserRole().getCarRentalCompany()).isNull();
        }
    }

    @Nested
    @DisplayName("removeRoleFromUserByUserId")
    class RemoveRole {

        @Test
        void resetsEmployeeOfOwnCompanyToCustomer() {
            CarRentalCompany company = company(1L, "AutoRent");
            User target = user(7L, "dan", UserRoleTypes.EMPLOYEE, company);
            when(userRepository.findById(7L)).thenReturn(Optional.of(target));

            staffService.removeRoleFromUserByUserId(7L, 1L);

            assertThat(target.getUserRole().getRole()).isEqualTo(UserRoleTypes.CUSTOMER);
            assertThat(target.getUserRole().getCarRentalCompany()).isNull();
        }

        @Test
        void rejectsStaffOfAnotherCompany() {
            User target = user(7L, "dan", UserRoleTypes.EMPLOYEE, company(2L, "Other"));
            when(userRepository.findById(7L)).thenReturn(Optional.of(target));

            assertThatThrownBy(() -> staffService.removeRoleFromUserByUserId(7L, 1L))
                    .isInstanceOf(InvalidRoleAssignmentException.class)
                    .hasMessageContaining("your own company");
            assertThat(target.getUserRole().getRole()).isEqualTo(UserRoleTypes.EMPLOYEE);
        }

        @Test
        void rejectsUserWithoutCompany() {
            User target = user(7L, "dan", UserRoleTypes.CUSTOMER, null);
            when(userRepository.findById(7L)).thenReturn(Optional.of(target));

            assertThatThrownBy(() -> staffService.removeRoleFromUserByUserId(7L, 1L))
                    .isInstanceOf(InvalidRoleAssignmentException.class);
        }
    }

    @Nested
    @DisplayName("searchUsers")
    class SearchUsers {

        @Test
        void blankTermReturnsEveryUser() {
            when(userRepository.findAll()).thenReturn(List.of(
                    user(1L, "ana", UserRoleTypes.CUSTOMER, null),
                    user(2L, "dan", UserRoleTypes.CUSTOMER, null)));

            List<UserDto> result = staffService.searchUsers("   ");

            assertThat(result).extracting(UserDto::getUsername).containsExactly("ana", "dan");
            verify(userRepository, never()).findByUsernameContainingIgnoreCase(any());
        }

        @Test
        void nullTermReturnsEveryUser() {
            when(userRepository.findAll()).thenReturn(List.of());

            assertThat(staffService.searchUsers(null)).isEmpty();
            verify(userRepository, never()).findByUsernameContainingIgnoreCase(any());
        }

        @Test
        void nonBlankTermDelegatesToSearchQuery() {
            when(userRepository.findByUsernameContainingIgnoreCase("an"))
                    .thenReturn(List.of(user(1L, "ana", UserRoleTypes.CUSTOMER, null)));

            List<UserDto> result = staffService.searchUsers("an");

            assertThat(result).hasSize(1);
            verify(userRepository, never()).findAll();
        }

        @Test
        void mapsCompanyNameWhenPresentAndNullOtherwise() {
            when(userRepository.findAll()).thenReturn(List.of(
                    user(1L, "bob", UserRoleTypes.EMPLOYEE, company(1L, "AutoRent")),
                    user(2L, "ana", UserRoleTypes.CUSTOMER, null)));

            List<UserDto> result = staffService.searchUsers(null);

            assertThat(result.get(0).getCompanyName()).isEqualTo("AutoRent");
            assertThat(result.get(1).getCompanyName()).isNull();
        }
    }

    @Nested
    @DisplayName("searchUsersForCompany")
    class SearchUsersForCompany {

        @Test
        void blankTermReturnsEmptyListWithoutTouchingRepositories() {
            assertThat(staffService.searchUsersForCompany("  ", 1L)).isEmpty();

            verify(carRentalCompanyRepository, never()).findById(any());
            verify(userRepository, never()).findSearchableUsersForCompany(any(), any());
        }

        @Test
        void rejectsUnknownCompany() {
            when(carRentalCompanyRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> staffService.searchUsersForCompany("ana", 99L))
                    .isInstanceOf(CompanyNotFoundException.class);
        }

        @Test
        void delegatesToScopedSearchQuery() {
            CarRentalCompany company = company(1L, "AutoRent");
            when(carRentalCompanyRepository.findById(1L)).thenReturn(Optional.of(company));
            when(userRepository.findSearchableUsersForCompany("ana", company))
                    .thenReturn(List.of(user(1L, "ana", UserRoleTypes.CUSTOMER, null)));

            List<UserDto> result = staffService.searchUsersForCompany("ana", 1L);

            assertThat(result).extracting(UserDto::getUsername).containsExactly("ana");
        }
    }

    @Nested
    @DisplayName("getEmployeesOfCompany")
    class GetEmployeesOfCompany {

        @Test
        void returnsEmployeesOfGivenCompany() {
            CarRentalCompany company = company(1L, "AutoRent");
            when(carRentalCompanyRepository.findById(1L)).thenReturn(Optional.of(company));
            when(userRepository.findByCarRentalCompanyAndRole(company, UserRoleTypes.EMPLOYEE))
                    .thenReturn(List.of(user(2L, "bob", UserRoleTypes.EMPLOYEE, company)));

            List<UserDto> result = staffService.getEmployeesOfCompany(1L);

            assertThat(result).extracting(UserDto::getUsername).containsExactly("bob");
            assertThat(result.get(0).getRole()).isEqualTo(UserRoleTypes.EMPLOYEE);
        }

        @Test
        void rejectsUnknownCompany() {
            when(carRentalCompanyRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> staffService.getEmployeesOfCompany(99L))
                    .isInstanceOf(CompanyNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("toggleAccountStatus")
    class ToggleAccountStatus {

        @Test
        void flipsEnabledFlagBothWays() {
            User target = user(7L, "dan", UserRoleTypes.CUSTOMER, null);
            when(userRepository.findById(7L)).thenReturn(Optional.of(target));

            staffService.toggleAccountStatus(7L);
            assertThat(target.isEnabled()).isFalse();

            staffService.toggleAccountStatus(7L);
            assertThat(target.isEnabled()).isTrue();
        }

        @Test
        void rejectsUnknownUser() {
            when(userRepository.findById(7L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> staffService.toggleAccountStatus(7L))
                    .isInstanceOf(UserNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("getRoleFromAuthentication")
    class GetRoleFromAuthentication {

        @Test
        void stripsRolePrefixAndMapsToEnum() {
            assertThat(staffService.getRoleFromAuthentication(auth("bob", "ROLE_MANAGER")))
                    .isEqualTo(UserRoleTypes.MANAGER);
            assertThat(staffService.getRoleFromAuthentication(auth("ana", "ROLE_CUSTOMER")))
                    .isEqualTo(UserRoleTypes.CUSTOMER);
        }

        @Test
        void failsOnAuthorityThatIsNotAKnownRole() {
            assertThatThrownBy(() -> staffService.getRoleFromAuthentication(auth("bob", "ROLE_GUEST")))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void failsWhenAuthenticationCarriesNoAuthority() {
            assertThatThrownBy(() -> staffService.getRoleFromAuthentication(auth("bob")))
                    .isInstanceOf(NoSuchElementException.class);
        }
    }

    @Nested
    @DisplayName("getCarRentalCompanyIdByUserId")
    class GetCarRentalCompanyId {

        @Test
        void returnsNullForCustomer() {
            when(userRepository.findByUsername("ana"))
                    .thenReturn(Optional.of(user(1L, "ana", UserRoleTypes.CUSTOMER, null)));

            assertThat(staffService.getCarRentalCompanyIdByUserId(auth("ana", "ROLE_CUSTOMER"))).isNull();
        }

        @Test
        void returnsNullForSuperAdmin() {
            when(userRepository.findByUsername("root"))
                    .thenReturn(Optional.of(user(1L, "root", UserRoleTypes.SUPER_ADMIN, null)));

            assertThat(staffService.getCarRentalCompanyIdByUserId(auth("root", "ROLE_SUPER_ADMIN"))).isNull();
        }

        @Test
        void returnsCompanyIdForStaff() {
            when(userRepository.findByUsername("bob"))
                    .thenReturn(Optional.of(user(2L, "bob", UserRoleTypes.EMPLOYEE, company(1L, "AutoRent"))));

            assertThat(staffService.getCarRentalCompanyIdByUserId(auth("bob", "ROLE_EMPLOYEE"))).isEqualTo(1L);
        }

        @Test
        void reportsMissingCompanyForStaffInsteadOfFailingWithNullPointer() {
            when(userRepository.findByUsername("bob"))
                    .thenReturn(Optional.of(user(2L, "bob", UserRoleTypes.EMPLOYEE, null)));

            assertThatThrownBy(() -> staffService.getCarRentalCompanyIdByUserId(auth("bob", "ROLE_EMPLOYEE")))
                    .isInstanceOf(CompanyRequiredException.class)
                    .hasMessageContaining("EMPLOYEE");
        }

        @Test
        void rejectsUnknownUser() {
            when(userRepository.findByUsername("ghost")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> staffService.getCarRentalCompanyIdByUserId(auth("ghost", "ROLE_CUSTOMER")))
                    .isInstanceOf(UserNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("getCompanyNameById")
    class GetCompanyNameById {

        @Test
        void returnsName() {
            when(carRentalCompanyRepository.findById(1L)).thenReturn(Optional.of(company(1L, "AutoRent")));

            assertThat(staffService.getCompanyNameById(1L)).isEqualTo("AutoRent");
        }

        @Test
        void rejectsUnknownCompany() {
            when(carRentalCompanyRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> staffService.getCompanyNameById(99L))
                    .isInstanceOf(CompanyNotFoundException.class);
        }
    }
}
