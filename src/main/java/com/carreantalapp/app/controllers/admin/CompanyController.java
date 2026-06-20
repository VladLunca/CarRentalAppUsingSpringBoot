package com.carreantalapp.app.controllers.admin;

import com.carreantalapp.app.dto.CarRentalCompanyDto;
import com.carreantalapp.app.services.CarRentalCompanyService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/rentalsCompanies")
@PreAuthorize("hasRole('SUPER_ADMIN')")
public class CompanyController {

    private final CarRentalCompanyService carRentalCompanyService;

    @Autowired
    public CompanyController(CarRentalCompanyService carRentalCompanyService) {
        this.carRentalCompanyService = carRentalCompanyService;
    }

    @GetMapping("/show")
    public String companies(Model model, @RequestParam(required = false) String searchTerm) {
        model.addAttribute("companies", carRentalCompanyService.findAll(searchTerm));
        model.addAttribute("searchTerm", searchTerm);
        return "admin/show-companies-table";
    }

    @GetMapping("/addCompany")
    public String showAddCompanyForm() {
        return "admin/show-company-form";
    }

    @PostMapping("/addCompany")
    public String addCompany(@Valid @ModelAttribute CarRentalCompanyDto dto,
                             BindingResult bindingResult,
                             RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("warning",
                    bindingResult.getAllErrors().get(0).getDefaultMessage());
            return "redirect:/admin/rentalsCompanies/addCompany";
        }
        carRentalCompanyService.addCompany(dto);
        return "redirect:/admin/rentalsCompanies/show";
    }

    @GetMapping("/updateCompany")
    public String showUpdateCompanyForm(@RequestParam Long companyId, Model model) {
        model.addAttribute("company", carRentalCompanyService.findById(companyId));
        return "admin/update-company-form";
    }

    @PostMapping("/processUpdateCompany")
    public String updateCompany(@RequestParam Long companyId,
                                @Valid @ModelAttribute CarRentalCompanyDto dto,
                                BindingResult bindingResult,
                                RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("warning",
                    bindingResult.getAllErrors().get(0).getDefaultMessage());
            return "redirect:/admin/rentalsCompanies/updateCompany?companyId=" + companyId;
        }
        carRentalCompanyService.updateCompany(companyId, dto);
        return "redirect:/admin/rentalsCompanies/show";
    }

    @PostMapping("/deleteCompany")
    public String deleteCompany(@RequestParam Long companyId) {
        carRentalCompanyService.deleteCompany(companyId);
        return "redirect:/admin/rentalsCompanies/show";
    }
}
