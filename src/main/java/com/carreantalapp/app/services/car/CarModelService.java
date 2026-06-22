package com.carreantalapp.app.services.car;

import com.carreantalapp.app.dto.CarModelDropdownDto;
import com.carreantalapp.app.dto.CarModelFormDto;
import com.carreantalapp.app.exceptions.CarModelInUseException;
import com.carreantalapp.app.model.CarModel;
import com.carreantalapp.app.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CarModelService {

    private final CarModelRepository carModelRepository;
    private final CarRepository carRepository;
    private final EngineRepository engineRepository;
    private final TransmissionRepository transmissionRepository;
    private final CarBodyRepository carBodyRepository;
    private final CategoryRepository categoryRepository;

    @Autowired
    public CarModelService(CarModelRepository carModelRepository,
                           CarRepository carRepository,
                           EngineRepository engineRepository,
                           TransmissionRepository transmissionRepository,
                           CarBodyRepository carBodyRepository,
                           CategoryRepository categoryRepository) {
        this.carModelRepository = carModelRepository;
        this.carRepository = carRepository;
        this.engineRepository = engineRepository;
        this.transmissionRepository = transmissionRepository;
        this.carBodyRepository = carBodyRepository;
        this.categoryRepository = categoryRepository;
    }

    @Transactional
    public void saveCarModel(CarModelFormDto dto) {
        CarModel carModel = new CarModel();
        carModel.setBrand(dto.getBrand());
        carModel.setModel(dto.getModel());
        carModel.setYear(dto.getYear());
        carModel.setPricePerDay(dto.getPricePerDay());
        carModel.setTraction(dto.getTraction());
        carModel.setFuelConsumption(dto.getFuelConsumption());
        carModel.setNumberOfLuggage(dto.getNumberOfLuggage());
        carModel.setEngine(engineRepository.findById(dto.getEngineId())
                .orElseThrow(() -> new RuntimeException("Engine not found: " + dto.getEngineId())));
        carModel.setTransmission(transmissionRepository.findById(dto.getTransmissionId())
                .orElseThrow(() -> new RuntimeException("Transmission not found: " + dto.getTransmissionId())));
        carModel.setCarBody(carBodyRepository.findById(dto.getCarBodyId())
                .orElseThrow(() -> new RuntimeException("Car body not found: " + dto.getCarBodyId())));
        carModel.setCategory(categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found: " + dto.getCategoryId())));
        carModelRepository.save(carModel);
    }

    @Transactional
    public void deleteCarModel(Long carModelId) {
        if (carRepository.existsByCarModelId(carModelId)) {
            throw new CarModelInUseException(carModelId);
        }
        carModelRepository.deleteById(carModelId);
    }

    @Transactional(readOnly = true)
    public List<CarModelDropdownDto> getAllCarModels() {
        return carModelRepository.findAll().stream()
                .map(m -> new CarModelDropdownDto(m.getCarModelId(), m.getBrand(), m.getModel(), m.getYear(), m.getPricePerDay()))
                .toList();
    }
}
