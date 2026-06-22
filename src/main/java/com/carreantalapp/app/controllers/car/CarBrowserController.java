package com.carreantalapp.app.controllers.car;

import com.carreantalapp.app.dto.CarFilterDto;
import com.carreantalapp.app.model.utils.CarBodyTypes;
import com.carreantalapp.app.model.utils.TransmissionTypes;
import com.carreantalapp.app.model.utils.TractionTypes;
import com.carreantalapp.app.services.StaffService;
import com.carreantalapp.app.services.car.CarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

@Controller
@RequestMapping("/cars")
public class CarBrowserController {

    private final CarService carService;
    private final StaffService staffService;

    @Autowired
    public CarBrowserController(CarService carService, StaffService staffService) {
        this.carService = carService;
        this.staffService = staffService;
    }

    @GetMapping("/showCars")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'MANAGER')")
    public String showCars(@ModelAttribute CarFilterDto filters, Authentication auth, Model model) {
        Long companyId = staffService.getCarRentalCompanyIdByUserId(auth);
        model.addAttribute("cars", carService.getCompanyCars(companyId, filters));
        model.addAttribute("filters", filters);
        addFilterOptions(model);
        return "cars/show-cars";
    }

    @GetMapping("/carsAvailableToRent")
    @PreAuthorize("hasAnyRole('CUSTOMER','MANAGER','EMPLOYEE')")
    public String carsAvailableToRent(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end,
            @ModelAttribute CarFilterDto filters, Model model) {
        if (start == null) start = LocalDate.now();
        if (end == null) end = start.plusDays(1);
        model.addAttribute("cars", carService.getCarsAvailableForDates(start, end, filters));
        model.addAttribute("start", start);
        model.addAttribute("end", end);
        model.addAttribute("filters", filters);
        addFilterOptions(model);
        return "cars/available-cars";
    }

    private void addFilterOptions(Model model) {
        model.addAttribute("allBrands", carService.getAllBrands());
        model.addAttribute("allBodyTypes", CarBodyTypes.values());
        model.addAttribute("allTransmissions", TransmissionTypes.values());
        model.addAttribute("allTractionTypes", TractionTypes.values());
        model.addAttribute("categories", carService.getAllCategories());
    }
}
