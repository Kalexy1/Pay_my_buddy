package com.pay_my_buddy.controller;

import com.pay_my_buddy.model.User;
import com.pay_my_buddy.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * Contrôleur d'authentification gérant l'inscription et la connexion des utilisateurs.
 * <p>
 * Cette classe permet aux utilisateurs de se connecter, de s'inscrire et de naviguer entre les pages d'authentification.
 * </p>
 */
@Controller
public class AuthController {

    private final UserService userService;

    /**
     * Constructeur de la classe AuthController.
     *
     * @param userService Service de gestion des utilisateurs.
     */
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Redirige la page d'accueil vers la page de connexion.
     *
     * @return Redirection vers la page de connexion.
     */
    @GetMapping("/")
    public String home() {
        return "redirect:/login";
    }

    /**
     * Affiche la page de connexion.
     * <p>
     * En cas d'échec de connexion, un message d'erreur est ajouté au modèle.
     * </p>
     *
     * @param error Paramètre facultatif indiquant une erreur de connexion.
     * @param model Modèle pour transmettre des attributs à la vue.
     * @return La vue de connexion.
     */
    @GetMapping("/login")
    public String showLoginPage(@RequestParam(value = "error", required = false) String error, Model model) {
        if (error != null) {
            model.addAttribute("error", "Identifiants invalides. Veuillez réessayer.");
        }
        return "login";
    }

    /**
     * Gère la tentative de connexion d'un utilisateur.
     * <p>
     * Vérifie les informations d'identification et redirige vers la page de transfert si elles sont valides.
     * Sinon, un message d'erreur est affiché.
     * </p>
     *
     * @param email    Email de l'utilisateur.
     * @param password Mot de passe de l'utilisateur.
     * @param model    Modèle pour transmettre des attributs à la vue.
     * @return Redirection vers la page de transfert si authentifié, sinon retour à la page de connexion.
     */
    @PostMapping("/login")
    public String login(@RequestParam String email, @RequestParam String password, Model model) {
        User user = userService.authenticate(email, password);
        if (user != null) {
            return "redirect:/transfer?user=" + user.getId();
        } else {
            model.addAttribute("error", "Email ou mot de passe incorrect.");
            return "login";
        }
    }

    /**
     * Affiche la page d'inscription.
     *
     * @return La vue d'inscription.
     */
    @GetMapping("/register")
    public String showRegisterPage() {
        return "register";
    }

    /**
     * Gère l'inscription d'un nouvel utilisateur.
     * <p>
     * En cas de succès, l'utilisateur est redirigé vers la page de connexion.
     * Si l'email est déjà utilisé, un message d'erreur est affiché.
     * </p>
     *
     * @param user  Objet {@link User} contenant les informations du nouvel utilisateur.
     * @param model Modèle pour transmettre des attributs à la vue.
     * @return Redirection vers la page de connexion si succès, sinon retour à la page d'inscription avec un message d'erreur.
     */
    @PostMapping("/register")
    public String register(@ModelAttribute User user, Model model) {
        try {
            userService.registerUser(user);
            return "redirect:/login";
        } catch (Exception e) {
            model.addAttribute("error", "Cet email est déjà utilisé.");
            return "register";
        }
    }
}
