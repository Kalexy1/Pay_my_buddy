package com.pay_my_buddy.controller;

import com.pay_my_buddy.model.User;
import com.pay_my_buddy.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;

@Controller
public class FriendController {

    private final UserService userService;

    public FriendController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/add-friend")
    public String showAddFriendPage(Authentication authentication, Model model) {
        String email = authentication.getName(); // Récupère l'email de l'utilisateur connecté
        User user = userService.getUserByEmail(email); // Récupère l'utilisateur via son email

        model.addAttribute("userId", user.getId());
        return "add_friend";
    }

    @PostMapping("/add-friend")
    public String addFriend(Authentication authentication, @RequestParam String friendEmail) {
        String email = authentication.getName();
        User user = userService.getUserByEmail(email);

        userService.addFriend(user.getId(), friendEmail);
        return "redirect:/transfer";
    }
}
