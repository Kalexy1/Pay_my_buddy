package com.pay_my_buddy.controller;

import com.pay_my_buddy.model.User;
import com.pay_my_buddy.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * Contrôleur gérant l'affichage et la mise à jour du profil utilisateur.
 * <p>
 * Cette classe permet d'afficher la page du profil de l'utilisateur connecté
 * et de mettre à jour ses informations (nom, email et mot de passe).
 * </p>
 */
@Controller
@RequestMapping("/profile")
public class ProfileController {

    private final UserService userService;

    /**
     * Constructeur de la classe ProfileController.
     *
     * @param userService Service de gestion des utilisateurs.
     */
    public ProfileController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Affiche la page du profil de l'utilisateur connecté.
     * <p>
     * Ajoute les informations de l'utilisateur et un message optionnel au modèle.
     * </p>
     *
     * @param authentication Informations d'authentification de l'utilisateur connecté.
     * @param model          Modèle pour transmettre des attributs à la vue.
     * @param message        Message optionnel à afficher sur la page.
     * @return La vue du profil utilisateur.
     */
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

    /**
     * Met à jour le profil de l'utilisateur connecté.
     * <p>
     * Permet de modifier le nom d'utilisateur, l'email et, si fourni, le mot de passe.
     * </p>
     *
     * @param authentication Informations d'authentification de l'utilisateur connecté.
     * @param username       Nouveau nom d'utilisateur.
     * @param newEmail       Nouveau email de l'utilisateur.
     * @param newPassword    Nouveau mot de passe (facultatif).
     * @return Redirection vers la page du profil avec un message de confirmation ou d'erreur.
     */
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
