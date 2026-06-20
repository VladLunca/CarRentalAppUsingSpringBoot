package com.carreantalapp.app.controllers.admin;

import com.carreantalapp.app.exceptions.CompanyRequiredException;
import com.carreantalapp.app.model.utils.UserRoleTypes;
import com.carreantalapp.app.services.CarRentalCompanyService;
import com.carreantalapp.app.services.StaffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('SUPER_ADMIN')")
public class AdminController {

    private final StaffService staffService;
    private final CarRentalCompanyService carRentalCompanyService;

    @Autowired
    public AdminController(StaffService staffService, CarRentalCompanyService carRentalCompanyService) {
        this.staffService = staffService;
        this.carRentalCompanyService = carRentalCompanyService;
    }

    @GetMapping("/staff")
    public String staff(Model model, @RequestParam(required = false) String searchTerm) {
        model.addAttribute("users", staffService.searchUsers(searchTerm));
        model.addAttribute("companies", carRentalCompanyService.findAll(""));
        model.addAttribute("searchTerm", searchTerm);
        return "admin/staff";
    }

    @PostMapping("/staff/assign-role")
    public String assignRole(@RequestParam Long userId,
                             @RequestParam(required = false) Long companyId,
                             @RequestParam UserRoleTypes role,
                             Authentication auth,
                             RedirectAttributes redirectAttributes) {
        try {
            staffService.addRoleToUserByUserId(userId, role, companyId, staffService.getRoleFromAuthentication(auth));
        } catch (CompanyRequiredException e) {
            redirectAttributes.addFlashAttribute("warning", e.getMessage());
        }
        return "redirect:/admin/staff";
    }

    @PostMapping("/staff/toggle-status")
    public String toggleStatus(@RequestParam Long userId) {
        staffService.toggleAccountStatus(userId);
        return "redirect:/admin/staff";
    }
}
