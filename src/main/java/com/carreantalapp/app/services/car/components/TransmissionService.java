package com.carreantalapp.app.services.car.components;

import com.carreantalapp.app.dto.TransmissionDto;
import com.carreantalapp.app.exceptions.TransmissionInUseException;
import com.carreantalapp.app.model.Transmission;
import com.carreantalapp.app.repositories.CarModelRepository;
import com.carreantalapp.app.repositories.TransmissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TransmissionService {

    private final TransmissionRepository transmissionRepository;
    private final CarModelRepository carModelRepository;

    @Autowired
    public TransmissionService(TransmissionRepository transmissionRepository,
                               CarModelRepository carModelRepository) {
        this.transmissionRepository = transmissionRepository;
        this.carModelRepository = carModelRepository;
    }

    private TransmissionDto toDto(Transmission t) {
        TransmissionDto dto = new TransmissionDto();
        dto.setTransmissionId(t.getId());
        dto.setTransmissionName(t.getTransmissionName());
        dto.setTransmissionType(t.getTransmissionType());
        dto.setNumberOfGears(t.getNumberOfGears());
        return dto;
    }

    @Transactional(readOnly = true)
    public List<TransmissionDto> getAllTransmissions() {
        return transmissionRepository.findAll().stream().map(this::toDto).toList();
    }

    @Transactional
    public void saveTransmission(TransmissionDto dto) {
        Transmission transmission = new Transmission();
        transmission.setTransmissionName(dto.getTransmissionName());
        transmission.setTransmissionType(dto.getTransmissionType());
        transmission.setNumberOfGears(dto.getNumberOfGears());
        transmissionRepository.save(transmission);
    }

    @Transactional
    public void deleteTransmission(Long transmissionId) {
        if (carModelRepository.existsByTransmissionId(transmissionId)) {
            throw new TransmissionInUseException(transmissionId);
        }
        transmissionRepository.deleteById(transmissionId);
    }
}
