package com.pay_my_buddy.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.pay_my_buddy.model.Compte;
import com.pay_my_buddy.model.User;
import com.pay_my_buddy.service.CompteService;
import com.pay_my_buddy.service.UserService;

import java.math.BigDecimal;

/**
 * Contrôleur REST pour la gestion des comptes utilisateurs.
 * <p>
 * Cette classe permet de consulter le solde, de créditer et de débiter un compte.
 * </p>
 */
@RestController
@RequestMapping("/comptes")
public class CompteController {

    private final CompteService compteService;
    private final UserService userService;

    /**
     * Constructeur de la classe CompteController.
     *
     * @param compteService Service de gestion des comptes.
     * @param userService   Service de gestion des utilisateurs.
     */
    public CompteController(CompteService compteService, UserService userService) {
        this.compteService = compteService;
        this.userService = userService;
    }

    /**
     * Récupère le compte de l'utilisateur connecté.
     *
     * @param authentication Informations d'authentification de l'utilisateur connecté.
     * @return {@link ResponseEntity} contenant les informations du compte ou un message d'erreur si introuvable.
     */
    @GetMapping
    public ResponseEntity<?> getCompteByUser(Authentication authentication) {
        String email = authentication.getName();
        User user = userService.getUserByEmail(email);

        try {
            Compte compte = compteService.getCompteByUser(user);
            return ResponseEntity.ok(compte);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body("Compte introuvable pour l'utilisateur.");
        }
    }

    /**
     * Ajoute du crédit au compte de l'utilisateur connecté.
     *
     * @param authentication Informations d'authentification de l'utilisateur connecté.
     * @param amount         Montant à ajouter au compte.
     * @return {@link ResponseEntity} contenant un message de succès ou d'erreur.
     */
    @PostMapping("/credit")
    public ResponseEntity<String> creditCompte(Authentication authentication, @RequestBody BigDecimal amount) {
        String email = authentication.getName();
        User user = userService.getUserByEmail(email);

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            return ResponseEntity.badRequest().body("Le montant doit être supérieur à 0.");
        }

        try {
            compteService.creditCompte(user, amount);
            return ResponseEntity.ok("Crédit ajouté avec succès.");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Une erreur est survenue lors du crédit.");
        }
    }

    /**
     * Débite le compte de l'utilisateur connecté.
     *
     * @param authentication Informations d'authentification de l'utilisateur connecté.
     * @param amount         Montant à débiter du compte.
     * @return {@link ResponseEntity} contenant un message de succès ou d'erreur.
     */
    @PostMapping("/debit")
    public ResponseEntity<String> debitCompte(Authentication authentication, @RequestBody BigDecimal amount) {
        String email = authentication.getName();
        User user = userService.getUserByEmail(email);

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            return ResponseEntity.badRequest().body("Le montant doit être supérieur à 0.");
        }

        try {
            compteService.debitCompte(user, amount);
            return ResponseEntity.ok("Débit effectué avec succès.");
        } catch (RuntimeException e) {
            if ("Solde insuffisant".equals(e.getMessage())) {
                return ResponseEntity.badRequest().body("Solde insuffisant.");
            }
            return ResponseEntity.internalServerError().body("Une erreur est survenue lors du débit.");
        }
    }
}
