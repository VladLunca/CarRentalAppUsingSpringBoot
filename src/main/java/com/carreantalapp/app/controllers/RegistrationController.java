package com.carreantalapp.app.controllers;

import com.carreantalapp.app.dto.WebUserDTO;
import com.carreantalapp.app.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/registration")
public class RegistrationController {
    private final UserService userService;

    @Autowired
    public RegistrationController(UserService userService) {
        this.userService = userService;
    }
    @InitBinder
    public void initBinder(WebDataBinder dataBinder) {
        dataBinder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
    }

    @GetMapping("/form")
    public String registrationForm(Model model){
        model.addAttribute("webUserDTO", new WebUserDTO());
        return "registration-form";
    }
    @PostMapping("/process")
    public String processRegistration(@Valid @ModelAttribute("webUserDTO") WebUserDTO webUserDTO, BindingResult bindingResult){
        if(!webUserDTO.getPassword().equals(webUserDTO.getConfirmPassword())){
            bindingResult.rejectValue("confirmPassword", "error.confirmPassword","Passwords do not match");
        }

        if(userService.usernameExists(webUserDTO.getUserName()))
        {
            bindingResult.rejectValue("userName", "error.userName","Username already exists");
        }

        if(bindingResult.hasErrors()){
            return "registration-form";
        }

        userService.register(webUserDTO);
        return "redirect:/registration/confirmation";
    }
    @GetMapping("/confirmation")
    public String showConfirmation() {
        return "registration-confirmation";
    }


}
