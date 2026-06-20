package com.carreantalapp.app.services;

import com.carreantalapp.app.dto.CarRentalCompanyDto;
import com.carreantalapp.app.exceptions.CompanyNotFoundException;
import com.carreantalapp.app.model.Address;
import com.carreantalapp.app.model.CarRentalCompany;
import com.carreantalapp.app.repositories.AddressRepository;
import com.carreantalapp.app.repositories.CarRentalCompanyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CarRentalCompanyService {
    private final CarRentalCompanyRepository carRentalCompanyRepository;
    private final AddressRepository addressRepository;

    @Autowired
    public CarRentalCompanyService(CarRentalCompanyRepository carRentalCompanyRepository,
                                   AddressRepository addressRepository) {
        this.carRentalCompanyRepository = carRentalCompanyRepository;
        this.addressRepository = addressRepository;
    }

    public List<CarRentalCompanyDto> findAll(String searchTerm) {
        if (searchTerm == null || searchTerm.isBlank())
            return carRentalCompanyRepository.findAll().stream().map(this::toDto).toList();
        return carRentalCompanyRepository.findByNameContainingIgnoreCase(searchTerm).stream().map(this::toDto).toList();
    }

    public CarRentalCompanyDto findById(Long id) {
        return toDto(carRentalCompanyRepository.findById(id).orElseThrow(() -> new CompanyNotFoundException(id)));
    }

    @Transactional
    public void addCompany(CarRentalCompanyDto dto) {
        Address address = new Address();
        address.setCityName(dto.getCityName());
        address.setStreetName(dto.getStreetName());
        address.setStreetNumber(dto.getStreetNumber());
        addressRepository.save(address);

        CarRentalCompany company = new CarRentalCompany();
        company.setName(dto.getName());
        company.setEmail(dto.getEmail());
        company.setPhoneNumber(dto.getPhoneNumber());
        company.setDescription(dto.getDescription());
        company.setAddress(address);
        carRentalCompanyRepository.save(company);
    }

    @Transactional
    public void updateCompany(Long id, CarRentalCompanyDto dto) {
        CarRentalCompany company = carRentalCompanyRepository.findById(id)
                .orElseThrow(() -> new CompanyNotFoundException(id));
        company.setName(dto.getName());
        company.setEmail(dto.getEmail());
        company.setPhoneNumber(dto.getPhoneNumber());
        company.setDescription(dto.getDescription());
        company.getAddress().setCityName(dto.getCityName());
        company.getAddress().setStreetName(dto.getStreetName());
        company.getAddress().setStreetNumber(dto.getStreetNumber());
    }

    @Transactional
    public void deleteCompany(Long id) {
        carRentalCompanyRepository.delete(carRentalCompanyRepository.findById(id).orElseThrow(() -> new CompanyNotFoundException(id)));
    }

    private CarRentalCompanyDto toDto(CarRentalCompany c) {
        CarRentalCompanyDto dto = new CarRentalCompanyDto();
        dto.setId(c.getId());
        dto.setName(c.getName());
        dto.setEmail(c.getEmail());
        dto.setPhoneNumber(c.getPhoneNumber());
        dto.setDescription(c.getDescription());
        dto.setCityName(c.getAddress().getCityName());
        dto.setStreetName(c.getAddress().getStreetName());
        dto.setStreetNumber(c.getAddress().getStreetNumber());
        return dto;
    }
}
