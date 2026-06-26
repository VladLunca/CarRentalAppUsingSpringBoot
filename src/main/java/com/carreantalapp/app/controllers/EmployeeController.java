package com.carreantalapp.app.controllers;

import com.carreantalapp.app.dto.CarFormDto;
import com.carreantalapp.app.dto.UpdateCarDto;
import com.carreantalapp.app.services.StaffService;
import com.carreantalapp.app.services.car.CarService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

@Controller
@RequestMapping("/employee")
@PreAuthorize("hasAnyRole('EMPLOYEE', 'MANAGER')")
public class EmployeeController {

    private final CarService carService;
    private final StaffService staffService;

    @Autowired
    public EmployeeController(CarService carService, StaffService staffService) {
        this.carService = carService;
        this.staffService = staffService;
    }

    @GetMapping("/addCarsForm")
    public String addCarsForm(Model model) {
        model.addAttribute("car", new CarFormDto());
        model.addAttribute("carModels", carService.getAllCarModels());
        return "cars/add-car-form";
    }

    @PostMapping("/processCarsForm")
    public String processCarsForm(@Valid @ModelAttribute("car") CarFormDto dto, BindingResult result,
                                  @RequestParam("imageFile") MultipartFile imageFile,
                                  Authentication auth, Model model) throws IOException {
        if (result.hasErrors()) {
            model.addAttribute("carModels", carService.getAllCarModels());
            return "cars/add-car-form";
        }
        Long companyId = staffService.getCarRentalCompanyIdByUserId(auth);
        carService.saveCarFromDto(dto, companyId, imageFile);
        return "redirect:/cars/showCars";
    }

    @GetMapping("/cars/updateCar")
    public String updateCarForm(@RequestParam Long carId, Authentication auth, Model model) {
        Long companyId = staffService.getCarRentalCompanyIdByUserId(auth);
        model.addAttribute("car", carService.toUpdateDto(carId, companyId));
        return "cars/update-car-form";
    }

    @PostMapping("/processUpdateCarsForm")
    public String processUpdateCarsForm(@Valid @ModelAttribute("car") UpdateCarDto dto, BindingResult result,
                                        @RequestParam("imageFile") MultipartFile imageFile,
                                        Authentication auth, Model model) throws IOException {
        if (result.hasErrors()) {
            return "cars/update-car-form";
        }
        Long companyId = staffService.getCarRentalCompanyIdByUserId(auth);
        carService.updateCar(dto, companyId, imageFile);
        return "redirect:/cars/showCars";
    }

    @PostMapping("/cars/toggleStatus")
    public String toggleStatus(@RequestParam Long carId, Authentication auth, RedirectAttributes redirectAttributes) {
        try {
            Long companyId = staffService.getCarRentalCompanyIdByUserId(auth);
            carService.toggleCarStatus(carId, companyId);
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("warning", e.getMessage());
        }
        return "redirect:/cars/showCars";
    }

    @PostMapping("/cars/deleteCar")
    public String deleteCar(@RequestParam Long carId, Authentication auth, RedirectAttributes redirectAttributes) {
        try {
            Long companyId = staffService.getCarRentalCompanyIdByUserId(auth);
            carService.deleteCar(carId, companyId);
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("warning", e.getMessage());
        }
        return "redirect:/cars/showCars";
    }
}
