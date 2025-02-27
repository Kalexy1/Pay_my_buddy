package com.pay_my_buddy.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.pay_my_buddy.model.Compte;
import com.pay_my_buddy.model.User;
import com.pay_my_buddy.service.CompteService;
import com.pay_my_buddy.service.UserService;

import java.math.BigDecimal;
import java.math.BigInteger;

@RestController
@RequestMapping("/comptes")
public class CompteController {

    private final CompteService compteService;
    private final UserService userService;

    public CompteController(CompteService compteService, UserService userService) {
        this.compteService = compteService;
        this.userService = userService;
    }

    /**
     * 📌 Consulter le solde du compte utilisateur connecté.
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
     * ➕ Ajouter du crédit au compte de l'utilisateur connecté.
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
     * ➖ Débiter le compte de l'utilisateur connecté.
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
