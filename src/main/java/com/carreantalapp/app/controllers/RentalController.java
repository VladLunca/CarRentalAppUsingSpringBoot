package com.carreantalapp.app.controllers;

import com.carreantalapp.app.dto.CarDto;
import com.carreantalapp.app.dto.RentalDto;
import com.carreantalapp.app.dto.RentalFormDto;
import com.carreantalapp.app.services.RentalService;
import com.carreantalapp.app.services.StaffService;
import com.carreantalapp.app.services.car.CarService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Controller
@RequestMapping("/rentals")
public class RentalController {

    private final RentalService rentalService;
    private final CarService carService;
    private final StaffService staffService;

    @Autowired
    public RentalController(RentalService rentalService, CarService carService, StaffService staffService) {
        this.rentalService = rentalService;
        this.carService = carService;
        this.staffService = staffService;
    }

    @GetMapping("/newRental")
    @PreAuthorize("hasAnyRole('CUSTOMER','MANAGER','EMPLOYEE')")
    public String newRentalForm(
            @RequestParam Long carId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end,
            Model model) {
        CarDto car = carService.getCarById(carId);
        int days = (int) ChronoUnit.DAYS.between(start, end);

        RentalFormDto form = new RentalFormDto();
        form.setCarId(carId);
        form.setStartDate(start);
        form.setEndDate(end);

        model.addAttribute("rentalForm", form);
        model.addAttribute("carDescription", car.getBrand() + " " + car.getModel() + " (" + car.getYear() + ")");
        model.addAttribute("startDate", start);
        model.addAttribute("endDate", end);
        model.addAttribute("totalPrice", days * car.getPricePerDay());
        return "rentals/new-rental-form";
    }

    @PostMapping("/processRentalForm")
    @PreAuthorize("hasAnyRole('CUSTOMER','MANAGER','EMPLOYEE')")
    public String processRentalForm(
            @Valid @ModelAttribute("rentalForm") RentalFormDto dto,
            BindingResult result,
            Authentication auth,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            CarDto car = carService.getCarById(dto.getCarId());
            int days = dto.getStartDate() != null && dto.getEndDate() != null
                    ? (int) ChronoUnit.DAYS.between(dto.getStartDate(), dto.getEndDate()) : 0;
            model.addAttribute("carDescription", car.getBrand() + " " + car.getModel() + " (" + car.getYear() + ")");
            model.addAttribute("startDate", dto.getStartDate());
            model.addAttribute("endDate", dto.getEndDate());
            model.addAttribute("totalPrice", days * car.getPricePerDay());
            return "rentals/new-rental-form";
        }
        try {
            rentalService.createRental(auth.getName(), dto);
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            CarDto car = carService.getCarById(dto.getCarId());
            int days = (int) ChronoUnit.DAYS.between(dto.getStartDate(), dto.getEndDate());
            model.addAttribute("carDescription", car.getBrand() + " " + car.getModel() + " (" + car.getYear() + ")");
            model.addAttribute("startDate", dto.getStartDate());
            model.addAttribute("endDate", dto.getEndDate());
            model.addAttribute("totalPrice", days * car.getPricePerDay());
            return "rentals/new-rental-form";
        }
        redirectAttributes.addFlashAttribute("success", "Rental submitted successfully. Waiting for approval.");
        return "redirect:/rentals/allRentals?view=client";
    }

    @GetMapping("/allRentals")
    public String allRentals(
            @RequestParam(defaultValue = "client") String view,
            @RequestParam(defaultValue = "false") boolean pendingRentals,
            @RequestParam(defaultValue = "false") boolean activeRentals,
            @RequestParam(defaultValue = "false") boolean completedRentals,
            @RequestParam(defaultValue = "false") boolean cancelledRentals,
            @RequestParam(defaultValue = "false") boolean driver,
            Authentication auth, Model model) {

        boolean isStaff = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_EMPLOYEE")
                        || a.getAuthority().equals("ROLE_MANAGER")
                        || a.getAuthority().equals("ROLE_SUPER_ADMIN"));

        if (!"client".equals(view) && !isStaff) {
            return "redirect:/rentals/allRentals?view=client";
        }

        List<RentalDto> rentals;
        if ("client".equals(view)) {
            rentals = rentalService.getRentalsForUser(
                    auth.getName(), pendingRentals, activeRentals, completedRentals, cancelledRentals, driver);
        } else {
            Long companyId = staffService.getCarRentalCompanyIdByUserId(auth);
            rentals = rentalService.getRentalsForCompany(
                    companyId, pendingRentals, activeRentals, completedRentals, cancelledRentals, driver);
        }

        model.addAttribute("rentals", rentals);
        model.addAttribute("view", view);
        model.addAttribute("pendingRentals", pendingRentals);
        model.addAttribute("activeRentals", activeRentals);
        model.addAttribute("completedRentals", completedRentals);
        model.addAttribute("cancelledRentals", cancelledRentals);
        model.addAttribute("driver", driver);
        return "rentals/all-rentals";
    }

    @PostMapping("/cancelRental")
    public String cancelRental(
            @RequestParam Long rentalId,
            @RequestParam(defaultValue = "client") String view,
            Authentication auth,
            RedirectAttributes redirectAttributes) {
        try {
            rentalService.cancelRental(rentalId, auth.getName());
            redirectAttributes.addFlashAttribute("success", "Rental cancelled.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("warning", e.getMessage());
        }
        return "redirect:/rentals/allRentals?view=" + view;
    }

    @PostMapping("/approveRental")
    @PreAuthorize("hasAnyRole('EMPLOYEE','MANAGER')")
    public String approveRental(
            @RequestParam Long rentalId,
            @RequestParam(defaultValue = "emp") String view,
            Authentication auth,
            RedirectAttributes redirectAttributes) {
        try {
            rentalService.approveRental(rentalId, auth.getName());
            redirectAttributes.addFlashAttribute("success", "Rental approved.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("warning", e.getMessage());
        }
        return "redirect:/rentals/allRentals?view=" + view;
    }
}
