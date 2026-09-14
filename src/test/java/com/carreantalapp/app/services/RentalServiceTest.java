package com.carreantalapp.app.services;

import com.carreantalapp.app.dto.RentalDto;
import com.carreantalapp.app.dto.RentalFormDto;
import com.carreantalapp.app.exceptions.RentalNotPendingException;
import com.carreantalapp.app.exceptions.UserNotFoundException;
import com.carreantalapp.app.model.Address;
import com.carreantalapp.app.model.Car;
import com.carreantalapp.app.model.CarModel;
import com.carreantalapp.app.model.CarRentalCompany;
import com.carreantalapp.app.model.Rental;
import com.carreantalapp.app.model.RentalLocation;
import com.carreantalapp.app.model.User;
import com.carreantalapp.app.model.UserDetails;
import com.carreantalapp.app.model.utils.CarStatus;
import com.carreantalapp.app.model.utils.RentalStatus;
import com.carreantalapp.app.model.utils.UserRoleTypes;
import com.carreantalapp.app.repositories.AddressRepository;
import com.carreantalapp.app.repositories.CarRepository;
import com.carreantalapp.app.repositories.RentalLocationRepository;
import com.carreantalapp.app.repositories.RentalRepository;
import com.carreantalapp.app.repositories.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RentalServiceTest {

    @Mock
    private RentalRepository rentalRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CarRepository carRepository;

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private RentalLocationRepository rentalLocationRepository;

    @InjectMocks
    private RentalService rentalService;

    private static final LocalDate TODAY = LocalDate.now();

    private CarRentalCompany company(Long id) {
        CarRentalCompany company = new CarRentalCompany();
        company.setId(id);
        company.setName("Company " + id);
        return company;
    }

    private CarModel carModel() {
        CarModel model = new CarModel();
        model.setCarModelId(1L);
        model.setBrand("Dacia");
        model.setModel("Logan");
        model.setYear(2022);
        model.setPricePerDay(100);
        return model;
    }

    private Car car(Long id, CarRentalCompany company) {
        Car car = new Car();
        car.setId(id);
        car.setCarModel(carModel());
        car.setCarRentalCompany(company);
        car.setLicencePlate("CJ01ABC");
        car.setColor("black");
        car.setStatus(CarStatus.AVAILABLE);
        return car;
    }

    private User user(String username, UserRoleTypes role, CarRentalCompany company) {
        User user = new User(
                username,
                "{noop}secret",
                new UserDetails("Ana", "Pop", "ana@gmail.com", "1234567890123", "0712345678"));
        user.getUserRole().setRole(role);
        user.getUserRole().setCarRentalCompany(company);
        return user;
    }

    private Address address(String city, String street, int number) {
        Address address = new Address();
        address.setCityName(city);
        address.setStreetName(street);
        address.setStreetNumber(number);
        return address;
    }

    private RentalLocation location(Address address, CarRentalCompany company) {
        RentalLocation location = new RentalLocation();
        location.setAddress(address);
        location.setCarRentalCompany(company);
        location.setName(address.getCityName());
        return location;
    }

    private Rental rental(Long id, Car car, User user, LocalDate start, LocalDate end, RentalStatus status) {
        RentalLocation location = location(
                address("Cluj-Napoca", "Memorandumului", 10), car.getCarRentalCompany());
        Rental rental = new Rental();
        rental.setId(id);
        rental.setCar(car);
        rental.setUser(user);
        rental.setPickupLocation(location);
        rental.setDropoffLocation(location);
        rental.setStartDate(start);
        rental.setEndDate(end);
        rental.setStatus(status);
        return rental;
    }

    private RentalFormDto form(Long carId, LocalDate start, LocalDate end) {
        RentalFormDto dto = new RentalFormDto();
        dto.setCarId(carId);
        dto.setStartDate(start);
        dto.setEndDate(end);
        dto.setPickupCity("Cluj-Napoca");
        dto.setPickupStreet("Memorandumului");
        dto.setPickupStreetNumber(10);
        dto.setDropoffCity("Cluj-Napoca");
        dto.setDropoffStreet("Memorandumului");
        dto.setDropoffStreetNumber(10);
        return dto;
    }

    @Nested
    @DisplayName("createRental")
    class CreateRental {

        @Test
        void rejectsMissingDates() {
            when(userRepository.findByUsername("ana"))
                    .thenReturn(Optional.of(user("ana", UserRoleTypes.CUSTOMER, null)));
            when(carRepository.findById(5L)).thenReturn(Optional.of(car(5L, company(1L))));

            RentalFormDto dto = form(5L, null, null);

            assertThatThrownBy(() -> rentalService.createRental("ana", dto))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("required");
            verify(rentalRepository, never()).save(any());
        }

        @Test
        void rejectsStartDateInThePast() {
            when(userRepository.findByUsername("ana"))
                    .thenReturn(Optional.of(user("ana", UserRoleTypes.CUSTOMER, null)));
            when(carRepository.findById(5L)).thenReturn(Optional.of(car(5L, company(1L))));

            RentalFormDto dto = form(5L, TODAY.minusDays(1), TODAY.plusDays(3));

            assertThatThrownBy(() -> rentalService.createRental("ana", dto))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("past");
            verify(rentalRepository, never()).save(any());
        }

        @Test
        void rejectsEndDateEqualToStartDate() {
            when(userRepository.findByUsername("ana"))
                    .thenReturn(Optional.of(user("ana", UserRoleTypes.CUSTOMER, null)));
            when(carRepository.findById(5L)).thenReturn(Optional.of(car(5L, company(1L))));

            RentalFormDto dto = form(5L, TODAY.plusDays(1), TODAY.plusDays(1));

            assertThatThrownBy(() -> rentalService.createRental("ana", dto))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("after");
            verify(rentalRepository, never()).save(any());
        }

        @Test
        void rejectsCarThatIsNotAvailable() {
            Car car = car(5L, company(1L));
            car.setStatus(CarStatus.IN_SERVICE);
            when(userRepository.findByUsername("ana"))
                    .thenReturn(Optional.of(user("ana", UserRoleTypes.CUSTOMER, null)));
            when(carRepository.findById(5L)).thenReturn(Optional.of(car));

            RentalFormDto dto = form(5L, TODAY.plusDays(1), TODAY.plusDays(4));

            assertThatThrownBy(() -> rentalService.createRental("ana", dto))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("not available");
            verify(rentalRepository, never()).save(any());
        }

        @Test
        void rejectsOverlappingBooking() {
            LocalDate start = TODAY.plusDays(1);
            LocalDate end = TODAY.plusDays(4);
            when(userRepository.findByUsername("ana"))
                    .thenReturn(Optional.of(user("ana", UserRoleTypes.CUSTOMER, null)));
            when(carRepository.findById(5L)).thenReturn(Optional.of(car(5L, company(1L))));
            when(rentalRepository.existsOverlappingRental(5L, start, end)).thenReturn(true);

            RentalFormDto dto = form(5L, start, end);

            assertThatThrownBy(() -> rentalService.createRental("ana", dto))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("already booked");
            verify(rentalRepository, never()).save(any());
        }

        @Test
        void rejectsUnknownUser() {
            when(userRepository.findByUsername("ghost")).thenReturn(Optional.empty());

            RentalFormDto dto = form(5L, TODAY.plusDays(1), TODAY.plusDays(4));

            assertThatThrownBy(() -> rentalService.createRental("ghost", dto))
                    .isInstanceOf(UserNotFoundException.class);
            verify(rentalRepository, never()).save(any());
        }

        @Test
        void savesPendingRentalAndReusesExistingLocation() {
            CarRentalCompany company = company(1L);
            Car car = car(5L, company);
            User customer = user("ana", UserRoleTypes.CUSTOMER, null);
            Address existingAddress = address("Cluj-Napoca", "Memorandumului", 10);
            RentalLocation existingLocation = location(existingAddress, company);
            LocalDate start = TODAY.plusDays(1);
            LocalDate end = TODAY.plusDays(4);

            when(userRepository.findByUsername("ana")).thenReturn(Optional.of(customer));
            when(carRepository.findById(5L)).thenReturn(Optional.of(car));
            when(rentalRepository.existsOverlappingRental(5L, start, end)).thenReturn(false);
            when(addressRepository.findByCityNameAndStreetNameAndStreetNumber(
                    "Cluj-Napoca", "Memorandumului", 10)).thenReturn(Optional.of(existingAddress));
            when(rentalLocationRepository.findByAddressAndCarRentalCompany(existingAddress, company))
                    .thenReturn(Optional.of(existingLocation));

            RentalFormDto dto = form(5L, start, end);
            dto.setWithDriver(true);
            dto.setChildSeat(true);

            rentalService.createRental("ana", dto);

            ArgumentCaptor<Rental> captor = ArgumentCaptor.forClass(Rental.class);
            verify(rentalRepository).save(captor.capture());
            Rental saved = captor.getValue();

            assertThat(saved.getStatus()).isEqualTo(RentalStatus.PENDING);
            assertThat(saved.getUser()).isSameAs(customer);
            assertThat(saved.getCar()).isSameAs(car);
            assertThat(saved.getStartDate()).isEqualTo(start);
            assertThat(saved.getEndDate()).isEqualTo(end);
            assertThat(saved.isWithDriver()).isTrue();
            assertThat(saved.isChildSeat()).isTrue();
            assertThat(saved.getPickupLocation()).isSameAs(existingLocation);
            assertThat(saved.getDropoffLocation()).isSameAs(existingLocation);
            verify(addressRepository, never()).save(any());
            verify(rentalLocationRepository, never()).save(any());
        }

        @Test
        void truncatesLongCityNameToFiftyCharacters() {
            CarRentalCompany company = company(1L);
            LocalDate start = TODAY.plusDays(1);
            LocalDate end = TODAY.plusDays(4);

            when(userRepository.findByUsername("ana"))
                    .thenReturn(Optional.of(user("ana", UserRoleTypes.CUSTOMER, null)));
            when(carRepository.findById(5L)).thenReturn(Optional.of(car(5L, company)));
            when(addressRepository.findByCityNameAndStreetNameAndStreetNumber(anyString(), anyString(), anyInt()))
                    .thenReturn(Optional.empty());
            when(addressRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
            when(rentalLocationRepository.findByAddressAndCarRentalCompany(any(), any()))
                    .thenReturn(Optional.empty());
            when(rentalLocationRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

            RentalFormDto dto = form(5L, start, end);
            dto.setPickupCity("A".repeat(60));

            rentalService.createRental("ana", dto);

            ArgumentCaptor<RentalLocation> captor = ArgumentCaptor.forClass(RentalLocation.class);
            verify(rentalLocationRepository, times(2)).save(captor.capture());

            assertThat(captor.getAllValues().get(0).getName()).hasSize(50);
            assertThat(captor.getAllValues().get(1).getName()).isEqualTo("Cluj-Napoca");
        }
    }

    @Nested
    @DisplayName("approveRental")
    class ApproveRental {

        @Test
        void activatesPendingRentalForOwnCompanyStaff() {
            CarRentalCompany company = company(1L);
            Car car = car(5L, company);
            Rental rental = rental(9L, car, user("ana", UserRoleTypes.CUSTOMER, null),
                    TODAY.plusDays(1), TODAY.plusDays(4), RentalStatus.PENDING);

            when(rentalRepository.findById(9L)).thenReturn(Optional.of(rental));
            when(userRepository.findByUsername("bob"))
                    .thenReturn(Optional.of(user("bob", UserRoleTypes.EMPLOYEE, company)));

            rentalService.approveRental(9L, "bob");

            assertThat(rental.getStatus()).isEqualTo(RentalStatus.ACTIVE);
        }

        @Test
        void rejectsRentalThatIsNotPending() {
            CarRentalCompany company = company(1L);
            Car car = car(5L, company);
            Rental rental = rental(9L, car, user("ana", UserRoleTypes.CUSTOMER, null),
                    TODAY.plusDays(1), TODAY.plusDays(4), RentalStatus.ACTIVE);

            when(rentalRepository.findById(9L)).thenReturn(Optional.of(rental));
            when(userRepository.findByUsername("bob"))
                    .thenReturn(Optional.of(user("bob", UserRoleTypes.EMPLOYEE, company)));

            assertThatThrownBy(() -> rentalService.approveRental(9L, "bob"))
                    .isInstanceOf(RentalNotPendingException.class);
            assertThat(rental.getStatus()).isEqualTo(RentalStatus.ACTIVE);
        }

        @Test
        void rejectsStaffFromAnotherCompany() {
            Car car = car(5L, company(1L));
            Rental rental = rental(9L, car, user("ana", UserRoleTypes.CUSTOMER, null),
                    TODAY.plusDays(1), TODAY.plusDays(4), RentalStatus.PENDING);

            when(rentalRepository.findById(9L)).thenReturn(Optional.of(rental));
            when(userRepository.findByUsername("bob"))
                    .thenReturn(Optional.of(user("bob", UserRoleTypes.EMPLOYEE, company(2L))));

            assertThatThrownBy(() -> rentalService.approveRental(9L, "bob"))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("your own company");
            assertThat(rental.getStatus()).isEqualTo(RentalStatus.PENDING);
        }

        @Test
        void rejectsStaffWithoutCompany() {
            Car car = car(5L, company(1L));
            Rental rental = rental(9L, car, user("ana", UserRoleTypes.CUSTOMER, null),
                    TODAY.plusDays(1), TODAY.plusDays(4), RentalStatus.PENDING);

            when(rentalRepository.findById(9L)).thenReturn(Optional.of(rental));
            when(userRepository.findByUsername("bob"))
                    .thenReturn(Optional.of(user("bob", UserRoleTypes.EMPLOYEE, null)));

            assertThatThrownBy(() -> rentalService.approveRental(9L, "bob"))
                    .isInstanceOf(IllegalStateException.class);
            assertThat(rental.getStatus()).isEqualTo(RentalStatus.PENDING);
        }
    }

    @Nested
    @DisplayName("cancelRental")
    class CancelRental {

        @Test
        void allowsCustomerToCancelOwnRental() {
            CarRentalCompany company = company(1L);
            User customer = user("ana", UserRoleTypes.CUSTOMER, null);
            Rental rental = rental(9L, car(5L, company), customer,
                    TODAY.plusDays(1), TODAY.plusDays(4), RentalStatus.PENDING);

            when(rentalRepository.findById(9L)).thenReturn(Optional.of(rental));
            when(userRepository.findByUsername("ana")).thenReturn(Optional.of(customer));

            rentalService.cancelRental(9L, "ana");

            assertThat(rental.getStatus()).isEqualTo(RentalStatus.CANCELLED);
        }

        @Test
        void rejectsCustomerCancellingSomeoneElsesRental() {
            CarRentalCompany company = company(1L);
            Rental rental = rental(9L, car(5L, company), user("ana", UserRoleTypes.CUSTOMER, null),
                    TODAY.plusDays(1), TODAY.plusDays(4), RentalStatus.PENDING);

            when(rentalRepository.findById(9L)).thenReturn(Optional.of(rental));
            when(userRepository.findByUsername("dan"))
                    .thenReturn(Optional.of(user("dan", UserRoleTypes.CUSTOMER, null)));

            assertThatThrownBy(() -> rentalService.cancelRental(9L, "dan"))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("your own rentals");
            assertThat(rental.getStatus()).isEqualTo(RentalStatus.PENDING);
        }

        @Test
        void rejectsRentalThatIsAlreadyCompleted() {
            Rental rental = rental(9L, car(5L, company(1L)), user("ana", UserRoleTypes.CUSTOMER, null),
                    TODAY.minusDays(10), TODAY.minusDays(5), RentalStatus.COMPLETED);

            when(rentalRepository.findById(9L)).thenReturn(Optional.of(rental));

            assertThatThrownBy(() -> rentalService.cancelRental(9L, "ana"))
                    .isInstanceOf(RentalNotPendingException.class);
            assertThat(rental.getStatus()).isEqualTo(RentalStatus.COMPLETED);
        }

        @Test
        void allowsStaffToCancelActiveRentalOfOwnCompany() {
            CarRentalCompany company = company(1L);
            Rental rental = rental(9L, car(5L, company), user("ana", UserRoleTypes.CUSTOMER, null),
                    TODAY.plusDays(1), TODAY.plusDays(4), RentalStatus.ACTIVE);

            when(rentalRepository.findById(9L)).thenReturn(Optional.of(rental));
            when(userRepository.findByUsername("bob"))
                    .thenReturn(Optional.of(user("bob", UserRoleTypes.MANAGER, company)));

            rentalService.cancelRental(9L, "bob");

            assertThat(rental.getStatus()).isEqualTo(RentalStatus.CANCELLED);
        }

        @Test
        void rejectsStaffFromAnotherCompany() {
            Rental rental = rental(9L, car(5L, company(1L)), user("ana", UserRoleTypes.CUSTOMER, null),
                    TODAY.plusDays(1), TODAY.plusDays(4), RentalStatus.PENDING);

            when(rentalRepository.findById(9L)).thenReturn(Optional.of(rental));
            when(userRepository.findByUsername("bob"))
                    .thenReturn(Optional.of(user("bob", UserRoleTypes.EMPLOYEE, company(2L))));

            assertThatThrownBy(() -> rentalService.cancelRental(9L, "bob"))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("your own company");
            assertThat(rental.getStatus()).isEqualTo(RentalStatus.PENDING);
        }
    }

    @Nested
    @DisplayName("getRentalsForUser")
    class GetRentalsForUser {

        @Test
        void mapsRentalToDtoWithPriceForNumberOfDays() {
            CarRentalCompany company = company(1L);
            User customer = user("ana", UserRoleTypes.CUSTOMER, null);
            Rental rental = rental(9L, car(5L, company), customer,
                    TODAY, TODAY.plusDays(3), RentalStatus.PENDING);

            when(userRepository.findByUsername("ana")).thenReturn(Optional.of(customer));
            when(rentalRepository.findByUser(customer)).thenReturn(List.of(rental));

            List<RentalDto> result =
                    rentalService.getRentalsForUser("ana", false, false, false, false, false);

            assertThat(result).hasSize(1);
            RentalDto dto = result.get(0);
            assertThat(dto.getId()).isEqualTo(9L);
            assertThat(dto.getCarDescription()).isEqualTo("Dacia Logan (2022)");
            assertThat(dto.getUsername()).isEqualTo("ana");
            assertThat(dto.getPickupLocation()).isEqualTo("Cluj-Napoca, Memorandumului 10");
            assertThat(dto.getDropoffLocation()).isEqualTo("Cluj-Napoca, Memorandumului 10");
            assertThat(dto.getTotalPrice()).isEqualTo(300);
        }

        @Test
        void returnsAllRentalsWhenNoStatusFilterIsSelected() {
            CarRentalCompany company = company(1L);
            User customer = user("ana", UserRoleTypes.CUSTOMER, null);
            List<Rental> rentals = List.of(
                    rental(1L, car(5L, company), customer, TODAY, TODAY.plusDays(2), RentalStatus.PENDING),
                    rental(2L, car(6L, company), customer, TODAY, TODAY.plusDays(2), RentalStatus.ACTIVE),
                    rental(3L, car(7L, company), customer, TODAY, TODAY.plusDays(2), RentalStatus.CANCELLED));

            when(userRepository.findByUsername("ana")).thenReturn(Optional.of(customer));
            when(rentalRepository.findByUser(customer)).thenReturn(rentals);

            List<RentalDto> result =
                    rentalService.getRentalsForUser("ana", false, false, false, false, false);

            assertThat(result).hasSize(3);
        }

        @Test
        void keepsOnlyRentalsMatchingSelectedStatuses() {
            CarRentalCompany company = company(1L);
            User customer = user("ana", UserRoleTypes.CUSTOMER, null);
            List<Rental> rentals = List.of(
                    rental(1L, car(5L, company), customer, TODAY, TODAY.plusDays(2), RentalStatus.PENDING),
                    rental(2L, car(6L, company), customer, TODAY, TODAY.plusDays(2), RentalStatus.ACTIVE),
                    rental(3L, car(7L, company), customer, TODAY, TODAY.plusDays(2), RentalStatus.CANCELLED));

            when(userRepository.findByUsername("ana")).thenReturn(Optional.of(customer));
            when(rentalRepository.findByUser(customer)).thenReturn(rentals);

            List<RentalDto> result =
                    rentalService.getRentalsForUser("ana", true, false, false, true, false);

            assertThat(result).extracting(RentalDto::getStatus)
                    .containsExactly(RentalStatus.PENDING, RentalStatus.CANCELLED);
        }

        @Test
        void keepsOnlyRentalsWithDriverWhenDriverOnlyIsSelected() {
            CarRentalCompany company = company(1L);
            User customer = user("ana", UserRoleTypes.CUSTOMER, null);
            Rental withDriver =
                    rental(1L, car(5L, company), customer, TODAY, TODAY.plusDays(2), RentalStatus.PENDING);
            withDriver.setWithDriver(true);
            Rental withoutDriver =
                    rental(2L, car(6L, company), customer, TODAY, TODAY.plusDays(2), RentalStatus.PENDING);

            when(userRepository.findByUsername("ana")).thenReturn(Optional.of(customer));
            when(rentalRepository.findByUser(customer)).thenReturn(List.of(withDriver, withoutDriver));

            List<RentalDto> result =
                    rentalService.getRentalsForUser("ana", false, false, false, false, true);

            assertThat(result).extracting(RentalDto::getId).containsExactly(1L);
        }

        @Test
        void rejectsUnknownUser() {
            when(userRepository.findByUsername("ghost")).thenReturn(Optional.empty());

            assertThatThrownBy(() ->
                    rentalService.getRentalsForUser("ghost", false, false, false, false, false))
                    .isInstanceOf(UserNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("completeExpiredRentals")
    class CompleteExpiredRentals {

        @Test
        void marksActiveRentalsPastEndDateAsCompleted() {
            CarRentalCompany company = company(1L);
            User customer = user("ana", UserRoleTypes.CUSTOMER, null);
            Rental expired = rental(1L, car(5L, company), customer,
                    TODAY.minusDays(10), TODAY.minusDays(2), RentalStatus.ACTIVE);

            when(rentalRepository.findByStatusAndEndDateBefore(eq(RentalStatus.ACTIVE), any()))
                    .thenReturn(List.of(expired));

            rentalService.completeExpiredRentals();

            assertThat(expired.getStatus()).isEqualTo(RentalStatus.COMPLETED);
        }

        @Test
        void doesNothingWhenNoRentalHasExpired() {
            when(rentalRepository.findByStatusAndEndDateBefore(eq(RentalStatus.ACTIVE), any()))
                    .thenReturn(List.of());

            rentalService.completeExpiredRentals();

            verify(rentalRepository).findByStatusAndEndDateBefore(eq(RentalStatus.ACTIVE), any());
        }

        @Test
        void startupListenerRunsTheSameSweep() {
            CarRentalCompany company = company(1L);
            Rental expired = rental(1L, car(5L, company), user("ana", UserRoleTypes.CUSTOMER, null),
                    TODAY.minusDays(10), TODAY.minusDays(2), RentalStatus.ACTIVE);
            when(rentalRepository.findByStatusAndEndDateBefore(eq(RentalStatus.ACTIVE), any()))
                    .thenReturn(List.of(expired));

            rentalService.completeExpiredRentalsOnStartup();

            assertThat(expired.getStatus()).isEqualTo(RentalStatus.COMPLETED);
        }
    }

    @Nested
    @DisplayName("getRentalsForCompany")
    class GetRentalsForCompany {

        @Test
        void returnsRentalsOfTheGivenCompanyMappedToDto() {
            CarRentalCompany company = company(1L);
            User customer = user("ana", UserRoleTypes.CUSTOMER, null);
            Rental rental = rental(9L, car(5L, company), customer,
                    TODAY, TODAY.plusDays(2), RentalStatus.PENDING);

            when(rentalRepository.findByCar_CarRentalCompany_Id(1L)).thenReturn(List.of(rental));

            List<RentalDto> result =
                    rentalService.getRentalsForCompany(1L, false, false, false, false, false);

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getId()).isEqualTo(9L);
            assertThat(result.get(0).getUsername()).isEqualTo("ana");
            assertThat(result.get(0).getTotalPrice()).isEqualTo(200);
        }

        @Test
        void appliesStatusFilters() {
            CarRentalCompany company = company(1L);
            User customer = user("ana", UserRoleTypes.CUSTOMER, null);
            when(rentalRepository.findByCar_CarRentalCompany_Id(1L)).thenReturn(List.of(
                    rental(1L, car(5L, company), customer, TODAY, TODAY.plusDays(2), RentalStatus.PENDING),
                    rental(2L, car(6L, company), customer, TODAY, TODAY.plusDays(2), RentalStatus.COMPLETED)));

            List<RentalDto> result =
                    rentalService.getRentalsForCompany(1L, false, false, true, false, false);

            assertThat(result).extracting(RentalDto::getStatus).containsExactly(RentalStatus.COMPLETED);
        }

        @Test
        void unknownCompanyYieldsEmptyListRatherThanAnError() {
            when(rentalRepository.findByCar_CarRentalCompany_Id(99L)).thenReturn(List.of());

            assertThat(rentalService.getRentalsForCompany(99L, false, false, false, false, false)).isEmpty();
        }
    }

    @Nested
    @DisplayName("getAllRentals")
    class GetAllRentals {

        @Test
        void returnsEveryRentalAcrossAllCompaniesWhenNoFilterIsSelected() {
            User customer = user("ana", UserRoleTypes.CUSTOMER, null);
            when(rentalRepository.findAll()).thenReturn(List.of(
                    rental(1L, car(5L, company(1L)), customer, TODAY, TODAY.plusDays(2), RentalStatus.PENDING),
                    rental(2L, car(6L, company(2L)), customer, TODAY, TODAY.plusDays(2), RentalStatus.CANCELLED)));

            List<RentalDto> result =
                    rentalService.getAllRentals(false, false, false, false, false);

            assertThat(result).extracting(RentalDto::getId).containsExactly(1L, 2L);
        }

        @Test
        void appliesTheSameFiltersAsTheCompanyScopedView() {
            User customer = user("ana", UserRoleTypes.CUSTOMER, null);
            when(rentalRepository.findAll()).thenReturn(List.of(
                    rental(1L, car(5L, company(1L)), customer, TODAY, TODAY.plusDays(2), RentalStatus.PENDING),
                    rental(2L, car(6L, company(2L)), customer, TODAY, TODAY.plusDays(2), RentalStatus.CANCELLED)));

            List<RentalDto> result =
                    rentalService.getAllRentals(true, false, false, false, false);

            assertThat(result).extracting(RentalDto::getStatus).containsExactly(RentalStatus.PENDING);
        }

        @Test
        void emptyRepositoryYieldsEmptyList() {
            when(rentalRepository.findAll()).thenReturn(List.of());

            assertThat(rentalService.getAllRentals(false, false, false, false, false)).isEmpty();
        }
    }
}
