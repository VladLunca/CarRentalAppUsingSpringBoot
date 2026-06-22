package com.carreantalapp.app.controllers.car.components;

import com.carreantalapp.app.dto.EngineDto;
import com.carreantalapp.app.model.utils.EngineTypes;
import com.carreantalapp.app.services.car.components.EngineService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import com.carreantalapp.app.exceptions.EngineInUseException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/employee")
@PreAuthorize("hasAnyRole('EMPLOYEE', 'MANAGER')")
public class EngineController {

    private final EngineService engineService;

    @Autowired
    public EngineController(EngineService engineService) {
        this.engineService = engineService;
    }

    @GetMapping("/addEngine")
    public String addEngineForm(Model model) {
        model.addAttribute("engine", new EngineDto());
        model.addAttribute("engineTypes", EngineTypes.values());
        model.addAttribute("engines", engineService.getAllEngines());
        return "cars/add-engine-form";
    }

    @PostMapping("/processEngineForm")
    public String processEngineForm(@Valid @ModelAttribute("engine") EngineDto dto,
                                    BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("engineTypes", EngineTypes.values());
            model.addAttribute("engines", engineService.getAllEngines());
            return "cars/add-engine-form";
        }
        engineService.saveEngine(dto);
        return "redirect:/employee/addEngine";
    }

    @PostMapping("/deleteEngine")
    public String deleteEngine(@RequestParam Long engineId, RedirectAttributes redirectAttributes) {
        try {
            engineService.deleteEngine(engineId);
        } catch (EngineInUseException e) {
            redirectAttributes.addFlashAttribute("warning", e.getMessage());
        }
        return "redirect:/employee/addEngine";
    }
}
