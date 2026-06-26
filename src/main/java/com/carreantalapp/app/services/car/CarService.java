package com.carreantalapp.app.services.car;

import com.carreantalapp.app.dto.*;

import com.carreantalapp.app.exceptions.CompanyNotFoundException;
import com.carreantalapp.app.model.*;
import com.carreantalapp.app.model.utils.CarStatus;
import com.carreantalapp.app.model.utils.RentalStatus;
import com.carreantalapp.app.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class CarService {

    private final CarRepository carRepository;
    private final CarRentalCompanyRepository carRentalCompanyRepository;
    private final CarModelRepository carModelRepository;
    private final CategoryRepository categoryRepository;
    private final RentalRepository rentalRepository;

    @Value("${app.upload-dir}")
    private String uploadDir;

    @Autowired
    public CarService(CarRepository carRepository,
                      CarRentalCompanyRepository carRentalCompanyRepository,
                      CarModelRepository carModelRepository,
                      CategoryRepository categoryRepository,
                      RentalRepository rentalRepository) {
        this.carRepository = carRepository;
        this.carRentalCompanyRepository = carRentalCompanyRepository;
        this.carModelRepository = carModelRepository;
        this.categoryRepository = categoryRepository;
        this.rentalRepository = rentalRepository;
    }

    private CarDto toDto(Car car) {
        CarModel m = car.getCarModel();
        return new CarDto(
                car.getId(),
                m.getBrand(),
                m.getModel(),
                m.getYear(),
                m.getCarBody().getNumberOfSeats(),
                car.getLicencePlate(),
                car.getColor(),
                car.getMileage(),
                m.getEngine().getHorsePower(),
                m.getEngine().getEngineType().name(),
                m.getTransmission().getTransmissionName(),
                m.getTraction().name(),
                m.getCategory().getCategoryName(),
                m.getPricePerDay(),
                car.getStatus(),
                car.getImage()
        );
    }

    @Transactional(readOnly = true)
    public List<CarDto> getCompanyCars(Long companyId, CarFilterDto filters) {
        CarRentalCompany company = carRentalCompanyRepository.findById(companyId)
                .orElseThrow(() -> new CompanyNotFoundException(companyId));
        Specification<Car> spec = Specification.where(CarSpec.ofCompany(company))
                .and(CarSpec.fromFilter(filters));
        return carRepository.findAll(spec).stream().map(this::toDto).toList();
    }




    @Transactional(readOnly = true)
    public CarDto getCarById(Long carId) {
        return toDto(carRepository.findById(carId)
                .orElseThrow(() -> new RuntimeException("Car not found: " + carId)));
    }

    @Transactional(readOnly = true)
    public List<CarDto> getAllAvailableCars(CarFilterDto filters) {
        Specification<Car> spec = Specification.where(CarSpec.statusAvailable())
                .and(CarSpec.fromFilter(filters));
        return carRepository.findAll(spec).stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public LocalDate getFirstAvailableDate() {
        return getFirstAvailableDateFrom(LocalDate.now());
    }

    @Transactional(readOnly = true)
    public LocalDate getFirstAvailableDateFrom(LocalDate from) {
        Specification<Car> available = CarSpec.statusAvailable();
        for (LocalDate d = from; d.isBefore(from.plusDays(365)); d = d.plusDays(1)) {
            if (carRepository.count(available.and(CarSpec.freeInPeriod(d, d.plusDays(1)))) > 0) return d;
        }
        return from;
    }

    @Transactional(readOnly = true)
    public LocalDate[] getNextAvailableWindow(LocalDate from) {
        LocalDate windowStart = getFirstAvailableDateFrom(from);
        Specification<Car> available = CarSpec.statusAvailable();
        LocalDate windowEnd = windowStart;
        for (LocalDate d = windowStart.plusDays(1); d.isBefore(windowStart.plusDays(60)); d = d.plusDays(1)) {
            if (carRepository.count(available.and(CarSpec.freeInPeriod(d, d.plusDays(1)))) > 0) {
                windowEnd = d;
            } else {
                break;
            }
        }
        return new LocalDate[]{windowStart, windowEnd};
    }

    @Transactional(readOnly = true)
    public List<LocalDate[]> getUpcomingAvailableWindows(int count) {
        List<LocalDate[]> windows = new ArrayList<>();
        Specification<Car> available = CarSpec.statusAvailable();
        LocalDate search = LocalDate.now();
        LocalDate limit = LocalDate.now().plusDays(365);
        while (windows.size() < count && !search.isAfter(limit)) {
            while (!search.isAfter(limit) &&
                    carRepository.count(available.and(CarSpec.freeInPeriod(search, search.plusDays(1)))) == 0) {
                search = search.plusDays(1);
            }
            if (search.isAfter(limit)) break;
            LocalDate windowStart = search;
            LocalDate windowEnd = search;
            while (!windowEnd.plusDays(1).isAfter(limit) &&
                    carRepository.count(available.and(CarSpec.freeInPeriod(windowEnd.plusDays(1), windowEnd.plusDays(2)))) > 0) {
                windowEnd = windowEnd.plusDays(1);
            }
            windows.add(new LocalDate[]{windowStart, windowEnd});
            search = windowEnd.plusDays(1);
        }
        return windows;
    }

    @Transactional(readOnly = true)
    public LocalDate[] getBestWindowWithin(LocalDate start, LocalDate end) {
        Specification<Car> available = CarSpec.statusAvailable();
        LocalDate windowStart = null;
        LocalDate windowEnd = null;
        LocalDate bestStart = null;
        LocalDate bestEnd = null;
        for (LocalDate d = start; !d.isAfter(end.minusDays(1)); d = d.plusDays(1)) {
            if (carRepository.count(available.and(CarSpec.freeInPeriod(d, d.plusDays(1)))) > 0) {
                if (windowStart == null) windowStart = d;
                windowEnd = d;
            } else {
                if (windowStart != null) {
                    if (bestStart == null || windowEnd.toEpochDay() - windowStart.toEpochDay()
                            > bestEnd.toEpochDay() - bestStart.toEpochDay()) {
                        bestStart = windowStart;
                        bestEnd = windowEnd;
                    }
                    windowStart = null;
                    windowEnd = null;
                }
            }
        }
        if (windowStart != null && (bestStart == null || windowEnd.toEpochDay() - windowStart.toEpochDay()
                > bestEnd.toEpochDay() - bestStart.toEpochDay())) {
            bestStart = windowStart;
            bestEnd = windowEnd;
        }
        return bestStart != null ? new LocalDate[]{bestStart, bestEnd} : null;
    }

    @Transactional(readOnly = true)
    public List<CarDto> getCarsAvailableForDates(LocalDate start, LocalDate end, CarFilterDto filters) {
        Specification<Car> spec = Specification.where(CarSpec.statusAvailable())
                .and(CarSpec.freeInPeriod(start, end))
                .and(CarSpec.fromFilter(filters));
        return carRepository.findAll(spec).stream().map(this::toDto).toList();
    }

    @Transactional
    public void saveCarFromDto(CarFormDto dto, Long companyId, MultipartFile imageFile) throws IOException {
        CarRentalCompany company = carRentalCompanyRepository.findById(companyId)
                .orElseThrow(() -> new CompanyNotFoundException(companyId));
        CarModel carModel = carModelRepository.findById(dto.getCarModelId())
                .orElseThrow(() -> new RuntimeException("Car model not found: " + dto.getCarModelId()));
        Car car = new Car();
        car.setCarModel(carModel);
        car.setCarRentalCompany(company);
        car.setLicencePlate(dto.getLicencePlate());
        car.setColor(dto.getColor());
        car.setMileage(dto.getMileage());
        if (imageFile != null && !imageFile.isEmpty()) {
            car.setImage(saveImage(imageFile));
        }
        carRepository.save(car);
    }

    @Transactional
    public void updateCar(UpdateCarDto dto, Long companyId, MultipartFile imageFile) throws IOException {
        Car car = carRepository.findById(dto.getCarId())
                .orElseThrow(() -> new RuntimeException("Car not found: " + dto.getCarId()));
        verifyCarBelongsToCompany(car, companyId);
        car.setLicencePlate(dto.getLicencePlate());
        car.setColor(dto.getColor());
        car.setMileage(dto.getMileage());
        car.setStatus(dto.getStatus());
        if (imageFile != null && !imageFile.isEmpty()) {
            car.setImage(saveImage(imageFile));
        }
        carRepository.save(car);
    }

    @Transactional
    public void deleteCar(Long carId, Long companyId) {
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new RuntimeException("Car not found: " + carId));
        verifyCarBelongsToCompany(car, companyId);
        if (rentalRepository.existsByCarAndStatusIn(car, List.of(RentalStatus.PENDING, RentalStatus.ACTIVE))) {
            throw new RuntimeException("Cannot delete a car with active or pending rentals");
        }
        carRepository.delete(car);
    }

    @Transactional
    public void toggleCarStatus(Long carId, Long companyId) {
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new RuntimeException("Car not found: " + carId));
        verifyCarBelongsToCompany(car, companyId);
        if (car.getStatus() == CarStatus.RENTED) {
            throw new RuntimeException("Cannot toggle status of a rented car");
        }
        car.setStatus(car.getStatus() == CarStatus.AVAILABLE ? CarStatus.IN_SERVICE : CarStatus.AVAILABLE);
        carRepository.save(car);
    }

    private void verifyCarBelongsToCompany(Car car, Long companyId) {
        if (companyId == null || car.getCarRentalCompany() == null
                || !car.getCarRentalCompany().getId().equals(companyId)) {
            throw new IllegalStateException("You can only manage cars of your own company.");
        }
    }

    @Transactional(readOnly = true)
    public UpdateCarDto toUpdateDto(Long carId, Long companyId) {
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new RuntimeException("Car not found: " + carId));
        verifyCarBelongsToCompany(car, companyId);
        UpdateCarDto dto = new UpdateCarDto();
        dto.setCarId(car.getId());
        dto.setLicencePlate(car.getLicencePlate());
        dto.setColor(car.getColor());
        dto.setMileage(car.getMileage());
        dto.setStatus(car.getStatus());
        dto.setCarModelBrand(car.getCarModel().getBrand());
        dto.setCarModelModel(car.getCarModel().getModel());
        dto.setCarModelYear(car.getCarModel().getYear());
        return dto;
    }

    @Transactional(readOnly = true)
    public List<String> getAllBrands() {
        return carModelRepository.findDistinctBrands();
    }

    @Transactional(readOnly = true)
    public List<CarModelDropdownDto> getAllCarModels() {
        return carModelRepository.findAll().stream()
                .map(m -> new CarModelDropdownDto(m.getCarModelId(), m.getBrand(), m.getModel(), m.getYear(), m.getPricePerDay()))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<CategoryDto> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(c -> {
                    CategoryDto dto = new CategoryDto();
                    dto.setId(c.getId());
                    dto.setCategoryName(c.getCategoryName());
                    return dto;
                })
                .toList();
    }

    private String saveImage(MultipartFile imageFile) throws IOException {
        String filename = UUID.randomUUID() + "_" + imageFile.getOriginalFilename();
        Path dest = Paths.get(uploadDir).resolve(filename);
        Files.createDirectories(dest.getParent());
        imageFile.transferTo(dest.toFile());
        return "/uploads/" + filename;
    }

}
