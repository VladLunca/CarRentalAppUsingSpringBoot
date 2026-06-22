package com.carreantalapp.app.controllers.car.components;

import com.carreantalapp.app.dto.CategoryDto;
import com.carreantalapp.app.exceptions.CategoryInUseException;
import com.carreantalapp.app.services.car.components.CategoryService;
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
public class CategoryController {

    private final CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/addCategory")
    public String addCategoryForm(Model model) {
        model.addAttribute("category", new CategoryDto());
        model.addAttribute("categories", categoryService.getAllCategories());
        return "cars/add-category-form";
    }

    @PostMapping("/proccesCategoryForm")
    public String proccesCategoryForm(@Valid @ModelAttribute("category") CategoryDto dto,
                                      BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("categories", categoryService.getAllCategories());
            return "cars/add-category-form";
        }
        categoryService.saveCategory(dto);
        return "redirect:/employee/addCategory";
    }

    @PostMapping("/deleteCategory")
    public String deleteCategory(@RequestParam Long categoryId, RedirectAttributes redirectAttributes) {
        try {
            categoryService.deleteCategory(categoryId);
        } catch (CategoryInUseException e) {
            redirectAttributes.addFlashAttribute("warning", e.getMessage());
        }
        return "redirect:/employee/addCategory";
    }
}
