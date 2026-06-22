package com.carreantalapp.app.controllers;

import com.carreantalapp.app.dto.ProfileEditDto;
import com.carreantalapp.app.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    private final UserService userService;

    @Autowired
    public ProfileController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/editProfile")
    public String editProfileForm(@AuthenticationPrincipal UserDetails principal, Model model) {
        model.addAttribute("webuser", userService.getProfileEditDto(principal.getUsername()));
        return "profile/edit-profile-form";
    }

    @PostMapping("/processEdit")
    public String processEdit(@AuthenticationPrincipal UserDetails principal,
                              @Valid @ModelAttribute("webuser") ProfileEditDto dto,
                              BindingResult result,
                              RedirectAttributes redirectAttributes,
                              Model model,
                              HttpServletRequest request) {
        if (result.hasErrors()) {
            return "profile/edit-profile-form";
        }
        boolean passwordChanged;
        try {
            passwordChanged = userService.updateProfile(principal.getUsername(), dto);
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "profile/edit-profile-form";
        }
        if (passwordChanged) {
            SecurityContextHolder.clearContext();
            HttpSession session = request.getSession(false);
            if (session != null) session.invalidate();
            return "redirect:/login?passwordChanged";
        }
        redirectAttributes.addFlashAttribute("success", "Profile updated successfully.");
        return "redirect:/profile/editProfile";
    }
}
