package com.carreantalapp.app.controllers;

import com.carreantalapp.app.exceptions.CompanyRequiredException;
import com.carreantalapp.app.exceptions.InvalidRoleAssignmentException;
import com.carreantalapp.app.model.utils.UserRoleTypes;
import com.carreantalapp.app.services.StaffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/manager")
@PreAuthorize("hasRole('MANAGER')")
public class ManageController {

    private final StaffService staffService;
    @Autowired
    public ManageController(StaffService staffService) {
        this.staffService = staffService;
    }
    @GetMapping("/staff")
    public String staff(@RequestParam(required = false) String query, Authentication auth, Model model) {
        Long companyId = staffService.getCarRentalCompanyIdByUserId(auth);
        model.addAttribute("employees", staffService.getEmployeesOfCompany(companyId));
        if (query != null && !query.isBlank()) {
            model.addAttribute("searchResults", staffService.searchUsersForCompany(query, companyId));
            model.addAttribute("query", query);
        }
        return "manager/staff";
    }

    @PostMapping("/staff/add-employee")
    public String addEmployee(@RequestParam Long userId, Authentication auth, RedirectAttributes redirectAttributes) {
        try {
            Long companyId = staffService.getCarRentalCompanyIdByUserId(auth);
            UserRoleTypes assignerRole = staffService.getRoleFromAuthentication(auth);
            staffService.addRoleToUserByUserId(userId, UserRoleTypes.EMPLOYEE, companyId, assignerRole);
        } catch (InvalidRoleAssignmentException | CompanyRequiredException e) {
            redirectAttributes.addFlashAttribute("warning", e.getMessage());
        }
        return "redirect:/manager/staff";
    }
    @PostMapping("/staff/remove-employee")
    public String removeEmployee(@RequestParam Long userId) {
        staffService.removeRoleFromUserByUserId(userId);
        return "redirect:/manager/staff";
    }


}
