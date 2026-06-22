package com.carreantalapp.app.services.car.components;

import com.carreantalapp.app.dto.CarBodyDto;
import com.carreantalapp.app.exceptions.CarBodyInUseException;
import com.carreantalapp.app.model.CarBody;
import com.carreantalapp.app.repositories.CarBodyRepository;
import com.carreantalapp.app.repositories.CarModelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CarBodyService {

    private final CarBodyRepository carBodyRepository;
    private final CarModelRepository carModelRepository;

    @Autowired
    public CarBodyService(CarBodyRepository carBodyRepository, CarModelRepository carModelRepository) {
        this.carBodyRepository = carBodyRepository;
        this.carModelRepository = carModelRepository;
    }

    private CarBodyDto toDto(CarBody carBody) {
        CarBodyDto dto = new CarBodyDto();
        dto.setId(carBody.getId());
        dto.setName(carBody.getName());
        dto.setNumberOfSeats(carBody.getNumberOfSeats());
        dto.setNumberOfDoors(carBody.getNumberOfDoors());
        return dto;
    }

    @Transactional(readOnly = true)
    public List<CarBodyDto> getAllCarBodies() {
        return carBodyRepository.findAll().stream().map(this::toDto).toList();
    }

    @Transactional
    public void saveCarBody(CarBodyDto dto) {
        CarBody carBody = new CarBody();
        carBody.setName(dto.getName());
        carBody.setNumberOfSeats(dto.getNumberOfSeats());
        carBody.setNumberOfDoors(dto.getNumberOfDoors());
        carBodyRepository.save(carBody);
    }

    @Transactional
    public void deleteCarBody(Long carBodyId) {
        if (carModelRepository.existsByCarBody_Id(carBodyId)) {
            throw new CarBodyInUseException(carBodyId);
        }
        carBodyRepository.deleteById(carBodyId);
    }
}
