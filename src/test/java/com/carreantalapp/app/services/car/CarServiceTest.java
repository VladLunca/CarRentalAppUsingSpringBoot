package com.carreantalapp.app.services.car;

import com.carreantalapp.app.dto.CarFilterDto;
import com.carreantalapp.app.dto.CarFormDto;
import com.carreantalapp.app.dto.UpdateCarDto;
import com.carreantalapp.app.exceptions.CompanyNotFoundException;
import com.carreantalapp.app.model.Car;
import com.carreantalapp.app.model.CarBody;
import com.carreantalapp.app.model.CarModel;
import com.carreantalapp.app.model.CarRentalCompany;
import com.carreantalapp.app.model.Category;
import com.carreantalapp.app.model.Engine;
import com.carreantalapp.app.model.Transmission;
import com.carreantalapp.app.model.utils.CarStatus;
import com.carreantalapp.app.model.utils.EngineTypes;
import com.carreantalapp.app.model.utils.RentalStatus;
import com.carreantalapp.app.model.utils.TractionTypes;
import com.carreantalapp.app.model.utils.TransmissionTypes;
import com.carreantalapp.app.repositories.CarModelRepository;
import com.carreantalapp.app.repositories.CarRentalCompanyRepository;
import com.carreantalapp.app.repositories.CarRepository;
import com.carreantalapp.app.repositories.CategoryRepository;
import com.carreantalapp.app.repositories.RentalRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CarServiceTest {

    @Mock
    private CarRepository carRepository;

    @Mock
    private CarRentalCompanyRepository carRentalCompanyRepository;

    @Mock
    private CarModelRepository carModelRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private RentalRepository rentalRepository;

    @InjectMocks
    private CarService carService;

    private static final LocalDate TODAY = LocalDate.now();

    private CarRentalCompany company(Long id) {
        CarRentalCompany company = new CarRentalCompany();
        company.setId(id);
        company.setName("Company " + id);
        return company;
    }

    private CarModel carModel() {
        Engine engine = new Engine();
        engine.setHorsePower(150);
        engine.setEngineType(EngineTypes.GASOLINE);

        CarBody body = new CarBody();
        body.setNumberOfSeats(5);

        Transmission transmission = new Transmission();
        transmission.setTransmissionName("Manual 6");
        transmission.setTransmissionType(TransmissionTypes.MANUAL);

        Category category = new Category();
        category.setCategoryName("Economy");

        CarModel model = new CarModel();
        model.setCarModelId(1L);
        model.setBrand("Dacia");
        model.setModel("Logan");
        model.setYear(2022);
        model.setPricePerDay(100);
        model.setTraction(TractionTypes.FWD);
        model.setEngine(engine);
        model.setCarBody(body);
        model.setTransmission(transmission);
        model.setCategory(category);
        return model;
    }

    private Car car(Long id, CarRentalCompany company, CarStatus status) {
        Car car = new Car();
        car.setId(id);
        car.setCarModel(carModel());
        car.setCarRentalCompany(company);
        car.setLicencePlate("CJ01ABC");
        car.setColor("black");
        car.setMileage(1000);
        car.setStatus(status);
        return car;
    }

    private UpdateCarDto updateDto(Long carId, CarStatus status) {
        UpdateCarDto dto = new UpdateCarDto();
        dto.setCarId(carId);
        dto.setLicencePlate("CJ99XYZ");
        dto.setColor("red");
        dto.setMileage(4242);
        dto.setStatus(status);
        return dto;
    }

    @Nested
    @DisplayName("Company ownership checks")
    class Ownership {

        @Test
        void updateCarRejectsCarOfAnotherCompany() throws IOException {
            when(carRepository.findById(5L)).thenReturn(Optional.of(car(5L, company(1L), CarStatus.AVAILABLE)));

            assertThatThrownBy(() -> carService.updateCar(updateDto(5L, CarStatus.AVAILABLE), 2L, null))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("your own company");
            verify(carRepository, never()).save(any());
        }

        @Test
        void deleteCarRejectsCarOfAnotherCompany() {
            when(carRepository.findById(5L)).thenReturn(Optional.of(car(5L, company(1L), CarStatus.AVAILABLE)));

            assertThatThrownBy(() -> carService.deleteCar(5L, 2L))
                    .isInstanceOf(IllegalStateException.class);
            verify(carRepository, never()).delete(any(Car.class));
        }

        @Test
        void toggleCarStatusRejectsCarOfAnotherCompany() {
            when(carRepository.findById(5L)).thenReturn(Optional.of(car(5L, company(1L), CarStatus.AVAILABLE)));

            assertThatThrownBy(() -> carService.toggleCarStatus(5L, 2L))
                    .isInstanceOf(IllegalStateException.class);
        }

        @Test
        void toUpdateDtoRejectsCarOfAnotherCompany() {
            when(carRepository.findById(5L)).thenReturn(Optional.of(car(5L, company(1L), CarStatus.AVAILABLE)));

            assertThatThrownBy(() -> carService.toUpdateDto(5L, 2L))
                    .isInstanceOf(IllegalStateException.class);
        }

        @Test
        void nullCompanyIdIsRejected() {
            when(carRepository.findById(5L)).thenReturn(Optional.of(car(5L, company(1L), CarStatus.AVAILABLE)));

            assertThatThrownBy(() -> carService.deleteCar(5L, null))
                    .isInstanceOf(IllegalStateException.class);
        }
    }

    @Nested
    @DisplayName("deleteCar")
    class DeleteCar {

        @Test
        void deletesCarWithoutOpenRentals() {
            Car car = car(5L, company(1L), CarStatus.AVAILABLE);
            when(carRepository.findById(5L)).thenReturn(Optional.of(car));
            when(rentalRepository.existsByCarAndStatusIn(
                    car, List.of(RentalStatus.PENDING, RentalStatus.ACTIVE))).thenReturn(false);

            carService.deleteCar(5L, 1L);

            verify(carRepository).delete(car);
        }

        @Test
        void refusesToDeleteCarWithPendingOrActiveRentals() {
            Car car = car(5L, company(1L), CarStatus.AVAILABLE);
            when(carRepository.findById(5L)).thenReturn(Optional.of(car));
            when(rentalRepository.existsByCarAndStatusIn(
                    car, List.of(RentalStatus.PENDING, RentalStatus.ACTIVE))).thenReturn(true);

            assertThatThrownBy(() -> carService.deleteCar(5L, 1L))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("active or pending rentals");
            verify(carRepository, never()).delete(any(Car.class));
        }

        @Test
        void rejectsUnknownCar() {
            when(carRepository.findById(5L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> carService.deleteCar(5L, 1L))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Car not found");
        }
    }

    @Nested
    @DisplayName("toggleCarStatus")
    class ToggleCarStatus {

        @Test
        void availableBecomesInService() {
            Car car = car(5L, company(1L), CarStatus.AVAILABLE);
            when(carRepository.findById(5L)).thenReturn(Optional.of(car));

            carService.toggleCarStatus(5L, 1L);

            assertThat(car.getStatus()).isEqualTo(CarStatus.IN_SERVICE);
        }

        @Test
        void inServiceBecomesAvailable() {
            Car car = car(5L, company(1L), CarStatus.IN_SERVICE);
            when(carRepository.findById(5L)).thenReturn(Optional.of(car));

            carService.toggleCarStatus(5L, 1L);

            assertThat(car.getStatus()).isEqualTo(CarStatus.AVAILABLE);
        }

        @Test
        void rentedCarCannotBeToggled() {
            Car car = car(5L, company(1L), CarStatus.RENTED);
            when(carRepository.findById(5L)).thenReturn(Optional.of(car));

            assertThatThrownBy(() -> carService.toggleCarStatus(5L, 1L))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("rented car");
            assertThat(car.getStatus()).isEqualTo(CarStatus.RENTED);
        }
    }

    @Nested
    @DisplayName("saveCarFromDto and updateCar")
    class SaveAndUpdate {

        @TempDir
        Path tempDir;

        private CarFormDto formDto() {
            CarFormDto dto = new CarFormDto();
            dto.setCarModelId(1L);
            dto.setLicencePlate("CJ10NEW");
            dto.setColor("blue");
            dto.setMileage(10);
            return dto;
        }

        @Test
        void savesCarWithoutImageWhenFileIsNull() throws IOException {
            CarRentalCompany company = company(1L);
            when(carRentalCompanyRepository.findById(1L)).thenReturn(Optional.of(company));
            when(carModelRepository.findById(1L)).thenReturn(Optional.of(carModel()));

            carService.saveCarFromDto(formDto(), 1L, null);

            ArgumentCaptor<Car> captor = ArgumentCaptor.forClass(Car.class);
            verify(carRepository).save(captor.capture());
            assertThat(captor.getValue().getImage()).isNull();
            assertThat(captor.getValue().getLicencePlate()).isEqualTo("CJ10NEW");
            assertThat(captor.getValue().getCarRentalCompany()).isSameAs(company);
        }

        @Test
        void savesCarWithoutImageWhenFileIsEmpty() throws IOException {
            when(carRentalCompanyRepository.findById(1L)).thenReturn(Optional.of(company(1L)));
            when(carModelRepository.findById(1L)).thenReturn(Optional.of(carModel()));

            carService.saveCarFromDto(formDto(), 1L,
                    new MockMultipartFile("image", "empty.png", "image/png", new byte[0]));

            ArgumentCaptor<Car> captor = ArgumentCaptor.forClass(Car.class);
            verify(carRepository).save(captor.capture());
            assertThat(captor.getValue().getImage()).isNull();
        }

        @Test
        void storesUploadedImageUnderUploadsPath() throws IOException {
            ReflectionTestUtils.setField(carService, "uploadDir", tempDir.toString());
            when(carRentalCompanyRepository.findById(1L)).thenReturn(Optional.of(company(1L)));
            when(carModelRepository.findById(1L)).thenReturn(Optional.of(carModel()));

            carService.saveCarFromDto(formDto(), 1L,
                    new MockMultipartFile("image", "car.png", "image/png", "bytes".getBytes()));

            ArgumentCaptor<Car> captor = ArgumentCaptor.forClass(Car.class);
            verify(carRepository).save(captor.capture());
            String image = captor.getValue().getImage();

            assertThat(image).startsWith("/uploads/").endsWith("_car.png");
            assertThat(Files.list(tempDir)).hasSize(1);
        }

        @Test
        void rejectsUnknownCompany() {
            when(carRentalCompanyRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> carService.saveCarFromDto(formDto(), 99L, null))
                    .isInstanceOf(CompanyNotFoundException.class);
        }

        @Test
        void rejectsUnknownCarModel() {
            when(carRentalCompanyRepository.findById(1L)).thenReturn(Optional.of(company(1L)));
            when(carModelRepository.findById(1L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> carService.saveCarFromDto(formDto(), 1L, null))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Car model not found");
        }

        @Test
        void updateOverwritesEditableFieldsAndKeepsImageWhenNoFileGiven() throws IOException {
            Car car = car(5L, company(1L), CarStatus.AVAILABLE);
            car.setImage("/uploads/old.png");
            when(carRepository.findById(5L)).thenReturn(Optional.of(car));

            carService.updateCar(updateDto(5L, CarStatus.IN_SERVICE), 1L, null);

            assertThat(car.getLicencePlate()).isEqualTo("CJ99XYZ");
            assertThat(car.getColor()).isEqualTo("red");
            assertThat(car.getMileage()).isEqualTo(4242);
            assertThat(car.getStatus()).isEqualTo(CarStatus.IN_SERVICE);
            assertThat(car.getImage()).isEqualTo("/uploads/old.png");
            verify(carRepository).save(car);
        }
    }

    @Nested
    @DisplayName("toUpdateDto")
    class ToUpdateDto {

        @Test
        void copiesCarAndModelFieldsIntoTheForm() {
            when(carRepository.findById(5L)).thenReturn(Optional.of(car(5L, company(1L), CarStatus.AVAILABLE)));

            UpdateCarDto dto = carService.toUpdateDto(5L, 1L);

            assertThat(dto.getCarId()).isEqualTo(5L);
            assertThat(dto.getLicencePlate()).isEqualTo("CJ01ABC");
            assertThat(dto.getColor()).isEqualTo("black");
            assertThat(dto.getMileage()).isEqualTo(1000);
            assertThat(dto.getStatus()).isEqualTo(CarStatus.AVAILABLE);
            assertThat(dto.getCarModelBrand()).isEqualTo("Dacia");
            assertThat(dto.getCarModelModel()).isEqualTo("Logan");
            assertThat(dto.getCarModelYear()).isEqualTo(2022);
        }
    }

    @Nested
    @DisplayName("getCompanyCars and getCarById")
    class Queries {

        @Test
        void companyCarsAreMappedToDto() {
            CarRentalCompany company = company(1L);
            when(carRentalCompanyRepository.findById(1L)).thenReturn(Optional.of(company));
            when(carRepository.findAll(any(Specification.class)))
                    .thenReturn(List.of(car(5L, company, CarStatus.AVAILABLE)));

            var result = carService.getCompanyCars(1L, new CarFilterDto());

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getBrand()).isEqualTo("Dacia");
            assertThat(result.get(0).getNumberOfSeats()).isEqualTo(5);
            assertThat(result.get(0).getPricePerDay()).isEqualTo(100);
            assertThat(result.get(0).getEngineType()).isEqualTo("GASOLINE");
            assertThat(result.get(0).getTransmissionName()).isEqualTo("Manual 6");
        }

        @Test
        void nullFilterIsTreatedAsNoFilter() {
            CarRentalCompany company = company(1L);
            when(carRentalCompanyRepository.findById(1L)).thenReturn(Optional.of(company));
            when(carRepository.findAll(any(Specification.class)))
                    .thenReturn(List.of(car(5L, company, CarStatus.AVAILABLE)));

            assertThat(carService.getCompanyCars(1L, null)).hasSize(1);
        }

        @Test
        void rejectsUnknownCompany() {
            when(carRentalCompanyRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> carService.getCompanyCars(99L, new CarFilterDto()))
                    .isInstanceOf(CompanyNotFoundException.class);
        }

        @Test
        void getCarByIdRejectsUnknownCar() {
            when(carRepository.findById(5L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> carService.getCarById(5L))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Car not found");
        }
    }

    @Nested
    @DisplayName("Availability windows")
    class AvailabilityWindows {

        @Test
        void firstAvailableDateIsTodayWhenSomethingIsFree() {
            when(carRepository.count(any(Specification.class))).thenReturn(1L);

            assertThat(carService.getFirstAvailableDateFrom(TODAY)).isEqualTo(TODAY);
        }

        @Test
        void firstAvailableDateFallsBackToStartWhenNothingIsEverFree() {
            when(carRepository.count(any(Specification.class))).thenReturn(0L);

            assertThat(carService.getFirstAvailableDateFrom(TODAY)).isEqualTo(TODAY);
        }

        @Test
        void nextAvailableWindowExtendsWhileCarsRemainFree() {
            when(carRepository.count(any(Specification.class)))
                    .thenReturn(1L, 1L, 1L, 0L, 0L);

            LocalDate[] window = carService.getNextAvailableWindow(TODAY);

            assertThat(window[0]).isEqualTo(TODAY);
            assertThat(window[1]).isEqualTo(TODAY.plusDays(2));
        }

        @Test
        void upcomingWindowsReturnsNothingWhenZeroRequested() {
            assertThat(carService.getUpcomingAvailableWindows(0)).isEmpty();
            verify(carRepository, never()).count(any(Specification.class));
        }

        @Test
        void bestWindowWithinReturnsNullWhenNothingIsFree() {
            when(carRepository.count(any(Specification.class))).thenReturn(0L);

            assertThat(carService.getBestWindowWithin(TODAY, TODAY.plusDays(5))).isNull();
        }

        @Test
        void bestWindowWithinPicksTheLongestContiguousBlock() {
            when(carRepository.count(any(Specification.class)))
                    .thenReturn(1L, 0L, 1L, 1L, 1L, 0L);

            LocalDate[] window = carService.getBestWindowWithin(TODAY, TODAY.plusDays(6));

            assertThat(window[0]).isEqualTo(TODAY.plusDays(2));
            assertThat(window[1]).isEqualTo(TODAY.plusDays(4));
        }

        @Test
        void bestWindowWithinKeepsBlockThatRunsToTheEnd() {
            when(carRepository.count(any(Specification.class)))
                    .thenReturn(0L, 1L, 1L);

            LocalDate[] window = carService.getBestWindowWithin(TODAY, TODAY.plusDays(3));

            assertThat(window[0]).isEqualTo(TODAY.plusDays(1));
            assertThat(window[1]).isEqualTo(TODAY.plusDays(2));
        }
    }
}
