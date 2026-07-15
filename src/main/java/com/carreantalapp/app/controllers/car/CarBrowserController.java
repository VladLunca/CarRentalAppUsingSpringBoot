package com.carreantalapp.app.controllers.car;

import com.carreantalapp.app.dto.CarDto;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

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

    @GetMapping("/chooseDates")
    @PreAuthorize("hasAnyRole('CUSTOMER','MANAGER','EMPLOYEE')")
    public String chooseDates(Model model) {
        LocalDate firstAvailable = carService.getFirstAvailableDate();
        model.addAttribute("minStart", firstAvailable);
        model.addAttribute("minEnd", firstAvailable.plusDays(1));
        model.addAttribute("availableWindows", carService.getUpcomingAvailableWindows(5));
        return "cars/choose-dates";
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
            @ModelAttribute CarFilterDto filters, Model model,
            RedirectAttributes redirectAttributes) {
        if (start == null || end == null) {
            redirectAttributes.addFlashAttribute("error", "Both dates are required.");
            return "redirect:/cars/chooseDates";
        }
        if (start.isBefore(LocalDate.now())) {
            redirectAttributes.addFlashAttribute("error", "Start date cannot be in the past.");
            return "redirect:/cars/chooseDates";
        }
        if (!end.isAfter(start)) {
            redirectAttributes.addFlashAttribute("error", "End date must be after start date.");
            return "redirect:/cars/chooseDates";
        }
        List<CarDto> carsNoFilters = carService.getCarsAvailableForDates(start, end, new CarFilterDto());
        if (carsNoFilters.isEmpty()) {
            LocalDate[] within = carService.getBestWindowWithin(start, end);
            LocalDate[] window = within != null ? within : carService.getNextAvailableWindow(end);
            String formattedEnd = (window[0].getYear() == window[1].getYear()
                    && window[0].getMonth() == window[1].getMonth())
                    ? String.valueOf(window[1].getDayOfMonth())
                    : window[1].toString();
            String suggestion = window[0].equals(window[1])
                    ? "Next available date: " + window[0]
                    : "Next available window: " + window[0] + " to " + formattedEnd;
            redirectAttributes.addFlashAttribute("error",
                    "No cars available from " + start + " to " + end + ". " + suggestion + ".");
            return "redirect:/cars/chooseDates";
        }

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
