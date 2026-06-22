package com.carreantalapp.app.controllers.car.components;

import com.carreantalapp.app.dto.TransmissionDto;
import com.carreantalapp.app.exceptions.TransmissionInUseException;
import com.carreantalapp.app.model.utils.TransmissionTypes;
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
public class TransmissionController {

    private final TransmissionService transmissionService;

    @Autowired
    public TransmissionController(TransmissionService transmissionService) {
        this.transmissionService = transmissionService;
    }

    @GetMapping("/addTransmission")
    public String addTransmissionForm(Model model) {
        model.addAttribute("transmission", new TransmissionDto());
        model.addAttribute("transmissionTypes", TransmissionTypes.values());
        model.addAttribute("transmissions", transmissionService.getAllTransmissions());
        return "cars/add-transmission-form";
    }

    @PostMapping("/processTransmissionForm")
    public String processTransmissionForm(@Valid @ModelAttribute("transmission") TransmissionDto dto,
                                          BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("transmissionTypes", TransmissionTypes.values());
            model.addAttribute("transmissions", transmissionService.getAllTransmissions());
            return "cars/add-transmission-form";
        }
        transmissionService.saveTransmission(dto);
        return "redirect:/employee/addTransmission";
    }

    @PostMapping("/deleteTransmission")
    public String deleteTransmission(@RequestParam Long transmissionId, RedirectAttributes redirectAttributes) {
        try {
            transmissionService.deleteTransmission(transmissionId);
        } catch (TransmissionInUseException e) {
            redirectAttributes.addFlashAttribute("warning", e.getMessage());
        }
        return "redirect:/employee/addTransmission";
    }
}
