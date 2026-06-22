package com.carreantalapp.app.services.car.components;

import com.carreantalapp.app.dto.EngineDto;
import com.carreantalapp.app.exceptions.EngineInUseException;
import com.carreantalapp.app.model.Engine;
import com.carreantalapp.app.repositories.CarModelRepository;
import com.carreantalapp.app.repositories.EngineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EngineService {

    private final EngineRepository engineRepository;
    private final CarModelRepository carModelRepository;

    @Autowired
    public EngineService(EngineRepository engineRepository, CarModelRepository carModelRepository) {
        this.engineRepository = engineRepository;
        this.carModelRepository = carModelRepository;
    }

    private EngineDto toDto(Engine engine) {
        EngineDto dto = new EngineDto();
        dto.setEngineId(engine.getId());
        dto.setHorsePower(engine.getHorsePower());
        dto.setEngineCapacity(engine.getEngineCapacity());
        dto.setEngineType(engine.getEngineType());
        return dto;
    }

    @Transactional(readOnly = true)
    public List<EngineDto> getAllEngines() {
        return engineRepository.findAll().stream().map(this::toDto).toList();
    }

    @Transactional
    public void saveEngine(EngineDto dto) {
        Engine engine = new Engine();
        engine.setHorsePower(dto.getHorsePower());
        engine.setEngineCapacity(dto.getEngineCapacity());
        engine.setEngineType(dto.getEngineType());
        engineRepository.save(engine);
    }

    @Transactional
    public void deleteEngine(Long engineId) {
        if (carModelRepository.existsByEngineId(engineId)) {
            throw new EngineInUseException(engineId);
        }
        engineRepository.deleteById(engineId);
    }
}
