package com.carreantalapp.app.controllers.car;

import com.carreantalapp.app.dto.CarModelFormDto;
import com.carreantalapp.app.exceptions.CarModelInUseException;
import com.carreantalapp.app.model.utils.TractionTypes;
import com.carreantalapp.app.services.car.CarModelService;
import com.carreantalapp.app.services.car.components.CarBodyService;
import com.carreantalapp.app.services.car.components.CategoryService;
import com.carreantalapp.app.services.car.components.EngineService;
import com.carreantalapp.app.services.car.components.TransmissionService;
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
public class CarModelController {

    private final CarModelService carModelService;
    private final EngineService engineService;
    private final TransmissionService transmissionService;
    private final CarBodyService carBodyService;
    private final CategoryService categoryService;

    @Autowired
    public CarModelController(CarModelService carModelService,
                              EngineService engineService,
                              TransmissionService transmissionService,
                              CarBodyService carBodyService,
                              CategoryService categoryService) {
        this.carModelService = carModelService;
        this.engineService = engineService;
        this.transmissionService = transmissionService;
        this.carBodyService = carBodyService;
        this.categoryService = categoryService;
    }

    @GetMapping("/addCarModelForm")
    public String addCarModelForm(Model model) {
        model.addAttribute("carModel", new CarModelFormDto());
        addDropdownData(model);
        return "cars/add-car-model-form";
    }

    @PostMapping("/processCarModelForm")
    public String processCarModelForm(@Valid @ModelAttribute("carModel") CarModelFormDto dto,
                                      BindingResult result, Model model) {
        if (result.hasErrors()) {
            addDropdownData(model);
            return "cars/add-car-model-form";
        }
        carModelService.saveCarModel(dto);
        return "redirect:/employee/addCarModelForm";
    }

    @PostMapping("/deleteCarModel")
    public String deleteCarModel(@RequestParam Long carModelId, RedirectAttributes redirectAttributes) {
        try {
            carModelService.deleteCarModel(carModelId);
        } catch (CarModelInUseException e) {
            redirectAttributes.addFlashAttribute("warning", e.getMessage());
        }
        return "redirect:/employee/addCarModelForm";
    }

    private void addDropdownData(Model model) {
        model.addAttribute("engines", engineService.getAllEngines());
        model.addAttribute("transmissions", transmissionService.getAllTransmissions());
        model.addAttribute("carBodies", carBodyService.getAllCarBodies());
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("carModels", carModelService.getAllCarModels());
        model.addAttribute("tractionTypes", TractionTypes.values());
    }
}
