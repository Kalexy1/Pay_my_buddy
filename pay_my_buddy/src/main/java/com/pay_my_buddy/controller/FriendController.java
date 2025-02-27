package com.pay_my_buddy.controller;

import com.pay_my_buddy.model.User;
import com.pay_my_buddy.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * Contrôleur gérant l'ajout d'amis pour l'utilisateur connecté.
 * <p>
 * Cette classe permet d'afficher la page d'ajout d'amis et de gérer l'ajout d'un ami à la liste d'un utilisateur.
 * </p>
 */
@Controller
public class FriendController {

    private final UserService userService;

    /**
     * Constructeur de la classe FriendController.
     *
     * @param userService Service de gestion des utilisateurs.
     */
    public FriendController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Affiche la page permettant d'ajouter un ami.
     *
     * @param authentication Informations d'authentification de l'utilisateur connecté.
     * @param model          Modèle pour transmettre des attributs à la vue.
     * @return La vue permettant d'ajouter un ami.
     */
    @GetMapping("/add-friend")
    public String showAddFriendPage(Authentication authentication, Model model) {
        String email = authentication.getName(); 
        User user = userService.getUserByEmail(email);

        model.addAttribute("userId", user.getId());
        return "add_friend";
    }

    /**
     * Ajoute un ami à la liste des contacts de l'utilisateur connecté.
     *
     * @param authentication Informations d'authentification de l'utilisateur connecté.
     * @param friendEmail    Email de l'ami à ajouter.
     * @return Redirection vers la page de transfert après ajout de l'ami.
     */
    @PostMapping("/add-friend")
    public String addFriend(Authentication authentication, @RequestParam String friendEmail) {
        String email = authentication.getName();
        User user = userService.getUserByEmail(email);

        userService.addFriend(user.getId(), friendEmail);
        return "redirect:/transfer";
    }
}
