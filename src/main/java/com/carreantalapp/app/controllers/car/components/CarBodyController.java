package com.carreantalapp.app.controllers.car.components;

import com.carreantalapp.app.dto.CarBodyDto;
import com.carreantalapp.app.exceptions.CarBodyInUseException;
import com.carreantalapp.app.model.utils.CarBodyTypes;
import com.carreantalapp.app.services.car.components.CarBodyService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/employee")
@PreAuthorize("hasAnyRole('EMPLOYEE', 'MANAGER')")
public class CarBodyController {

    private final CarBodyService carBodyService;

    @Autowired
    public CarBodyController(CarBodyService carBodyService) {
        this.carBodyService = carBodyService;
    }

    @GetMapping("/addCarBody")
    public String addCarBodyForm(Model model) {
        model.addAttribute("carBody", new CarBodyDto());
        model.addAttribute("carBodyTypes", CarBodyTypes.values());
        model.addAttribute("carBodies", carBodyService.getAllCarBodies());
        return "cars/add-car-body-form";
    }

    @PostMapping("/processCarBodyForm")
    public String processCarBodyForm(@Valid @ModelAttribute("carBody") CarBodyDto dto,
                                     BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("carBodyTypes", CarBodyTypes.values());
            model.addAttribute("carBodies", carBodyService.getAllCarBodies());
            return "cars/add-car-body-form";
        }
        carBodyService.saveCarBody(dto);
        return "redirect:/employee/addCarBody";
    }

    @PostMapping("/deleteCarBody")
    public String deleteCarBody(@RequestParam Long carBodyId, RedirectAttributes redirectAttributes) {
        try {
            carBodyService.deleteCarBody(carBodyId);
        } catch (CarBodyInUseException e) {
            redirectAttributes.addFlashAttribute("warning", e.getMessage());
        }
        return "redirect:/employee/addCarBody";
    }
}
