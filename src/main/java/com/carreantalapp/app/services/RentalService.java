package com.carreantalapp.app.services;

import com.carreantalapp.app.dto.RentalDto;
import com.carreantalapp.app.dto.RentalFormDto;
import com.carreantalapp.app.exceptions.RentalNotFoundException;
import com.carreantalapp.app.exceptions.RentalNotPendingException;
import com.carreantalapp.app.exceptions.UserNotFoundException;
import com.carreantalapp.app.model.*;
import com.carreantalapp.app.model.utils.RentalStatus;
import com.carreantalapp.app.model.utils.UserRoleTypes;
import com.carreantalapp.app.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class RentalService {

    private final RentalRepository rentalRepository;
    private final UserRepository userRepository;
    private final CarRepository carRepository;
    private final AddressRepository addressRepository;
    private final RentalLocationRepository rentalLocationRepository;

    @Autowired
    public RentalService(RentalRepository rentalRepository,
                         UserRepository userRepository,
                         CarRepository carRepository,
                         AddressRepository addressRepository,
                         RentalLocationRepository rentalLocationRepository) {
        this.rentalRepository = rentalRepository;
        this.userRepository = userRepository;
        this.carRepository = carRepository;
        this.addressRepository = addressRepository;
        this.rentalLocationRepository = rentalLocationRepository;
    }

    private RentalDto toDto(Rental rental) {
        CarModel model = rental.getCar().getCarModel();
        String carDescription = model.getBrand() + " " + model.getModel() + " (" + model.getYear() + ")";
        String pickup = formatAddress(rental.getPickupLocation().getAddress());
        String dropoff = formatAddress(rental.getDropoffLocation().getAddress());
        int days = (int) ChronoUnit.DAYS.between(rental.getStartDate(), rental.getEndDate());
        int totalPrice = days * model.getPricePerDay();
        return new RentalDto(
                rental.getId(),
                carDescription,
                rental.getUser().getUsername(),
                rental.getStartDate(),
                rental.getEndDate(),
                pickup,
                dropoff,
                rental.isWithDriver(),
                rental.isChildSeat(),
                rental.getStatus(),
                totalPrice
        );
    }

    private String formatAddress(Address address) {
        return address.getCityName() + ", " + address.getStreetName() + " " + address.getStreetNumber();
    }

    private List<RentalDto> applyFilters(List<Rental> rentals,
                                         boolean pending, boolean active,
                                         boolean completed, boolean cancelled,
                                         boolean driverOnly) {
        boolean anyStatus = pending || active || completed || cancelled;
        return rentals.stream()
                .filter(r -> !anyStatus
                        || (pending && r.getStatus() == RentalStatus.PENDING)
                        || (active && r.getStatus() == RentalStatus.ACTIVE)
                        || (completed && r.getStatus() == RentalStatus.COMPLETED)
                        || (cancelled && r.getStatus() == RentalStatus.CANCELLED))
                .filter(r -> !driverOnly || r.isWithDriver())
                .map(this::toDto)
                .toList();
    }

    private RentalLocation buildLocation(String city, String street, int number, CarRentalCompany company) {
        Address address = addressRepository
                .findByCityNameAndStreetNameAndStreetNumber(city, street, number)
                .orElseGet(() -> {
                    Address a = new Address();
                    a.setCityName(city);
                    a.setStreetName(street);
                    a.setStreetNumber(number);
                    return addressRepository.save(a);
                });
        return rentalLocationRepository
                .findByAddressAndCarRentalCompany(address, company)
                .orElseGet(() -> {
                    RentalLocation loc = new RentalLocation();
                    loc.setAddress(address);
                    loc.setCarRentalCompany(company);
                    loc.setName(city.length() <= 50 ? city : city.substring(0, 50));
                    return rentalLocationRepository.save(loc);
                });
    }

    @Transactional
    public void createRental(String username, RentalFormDto dto) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));
        Car car = carRepository.findById(dto.getCarId())
                .orElseThrow(() -> new RuntimeException("Car not found: " + dto.getCarId()));

        RentalLocation pickup = buildLocation(
                dto.getPickupCity(), dto.getPickupStreet(), dto.getPickupStreetNumber(),
                car.getCarRentalCompany());
        RentalLocation dropoff = buildLocation(
                dto.getDropoffCity(), dto.getDropoffStreet(), dto.getDropoffStreetNumber(),
                car.getCarRentalCompany());

        Rental rental = new Rental();
        rental.setCar(car);
        rental.setUser(user);
        rental.setPickupLocation(pickup);
        rental.setDropoffLocation(dropoff);
        rental.setStartDate(dto.getStartDate());
        rental.setEndDate(dto.getEndDate());
        rental.setWithDriver(dto.isWithDriver());
        rental.setChildSeat(dto.isChildSeat());
        rental.setStatus(RentalStatus.PENDING);
        rentalRepository.save(rental);
    }

    @Transactional(readOnly = true)
    public List<RentalDto> getRentalsForUser(String username,
                                             boolean pending, boolean active,
                                             boolean completed, boolean cancelled,
                                             boolean driverOnly) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));
        return applyFilters(rentalRepository.findByUser(user), pending, active, completed, cancelled, driverOnly);
    }

    @Transactional(readOnly = true)
    public List<RentalDto> getRentalsForCompany(Long companyId,
                                                boolean pending, boolean active,
                                                boolean completed, boolean cancelled,
                                                boolean driverOnly) {
        return applyFilters(
                rentalRepository.findByCar_CarRentalCompany_Id(companyId),
                pending, active, completed, cancelled, driverOnly);
    }

    @Transactional
    public void approveRental(Long rentalId) {
        Rental rental = rentalRepository.findById(rentalId)
                .orElseThrow(() -> new RentalNotFoundException(rentalId));
        if (rental.getStatus() != RentalStatus.PENDING)
            throw new RentalNotPendingException("approve");
        rental.setStatus(RentalStatus.ACTIVE);
    }

    @Transactional
    public void cancelRental(Long rentalId, String username) {
        Rental rental = rentalRepository.findById(rentalId)
                .orElseThrow(() -> new RentalNotFoundException(rentalId));
        if (rental.getStatus() != RentalStatus.PENDING && rental.getStatus() != RentalStatus.ACTIVE)
            throw new RentalNotPendingException("cancel");
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));
        if (user.getUserRole().getRole() == UserRoleTypes.CUSTOMER
                && !rental.getUser().getUsername().equals(username))
            throw new IllegalStateException("You can only cancel your own rentals.");
        rental.setStatus(RentalStatus.CANCELLED);
    }

    @Transactional(readOnly = true)
    public List<RentalDto> getAllRentals() {
        return rentalRepository.findAll().stream().map(this::toDto).toList();
    }

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void completeExpiredRentalsOnStartup() {
        completeExpiredRentals();
    }

    @Scheduled(cron = "0 0 0 * * *", zone = "Europe/Bucharest")
    @Transactional
    public void completeExpiredRentals() {
        rentalRepository.findByStatusAndEndDateBefore(RentalStatus.ACTIVE, LocalDate.now())
                .forEach(r -> r.setStatus(RentalStatus.COMPLETED));
    }
}
