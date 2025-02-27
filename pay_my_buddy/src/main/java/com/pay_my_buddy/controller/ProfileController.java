package com.pay_my_buddy.controller;

import com.pay_my_buddy.model.User;
import com.pay_my_buddy.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    private final UserService userService;

    public ProfileController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String showProfilePage(Authentication authentication, Model model,
                                  @RequestParam(value = "message", required = false) String message) {
        String email = authentication.getName();
        User user = userService.getUserByEmail(email);
        model.addAttribute("user", user);

        if (message != null) {
            model.addAttribute("message", message);
        }

        return "profile";
    }

    @PostMapping("/update")
    public String updateProfile(Authentication authentication,
                                @RequestParam String username,
                                @RequestParam String newEmail,
                                @RequestParam(required = false) String newPassword) {
        String email = authentication.getName();
        User user = userService.getUserByEmail(email);

        try {
            userService.updateUser(user.getId(), username, newEmail);

            if (newPassword != null && !newPassword.isEmpty()) {
                userService.updatePassword(user.getId(), newPassword);
            }

            return "redirect:/profile?message=Mise à jour réussie !";
        } catch (IllegalArgumentException e) {
            return "redirect:/profile?message=" + e.getMessage();
        }
    }
}
