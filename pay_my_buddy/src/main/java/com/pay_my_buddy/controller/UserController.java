package com.pay_my_buddy.controller;

import com.pay_my_buddy.model.User;
import com.pay_my_buddy.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.math.BigInteger;
import java.util.List;

@RestController
@RequestMapping("/users")
@Validated
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 🔎 Récupérer un utilisateur par son email.
     */
    @GetMapping("/{email}")
    public ResponseEntity<?> getUserByEmail(@PathVariable String email) {
        logger.info("Recherche de l'utilisateur avec l'email: {}", email);

        try {
            User user = userService.getUserByEmail(email);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            logger.error("Utilisateur non trouvé : {}", e.getMessage());
            return ResponseEntity.badRequest().body("Utilisateur non trouvé.");
        }
    }

    /**
     * ➕ Ajouter un ami à l'utilisateur.
     */
    @PostMapping("/{userId}/addFriend/{friendEmail}")
    public ResponseEntity<?> addFriend(@PathVariable BigInteger userId, @PathVariable String friendEmail) {
        logger.info("Ajout d'un ami: utilisateur {} à l'ami {}", userId, friendEmail);

        try {
            userService.addFriend(userId, friendEmail);
            return ResponseEntity.ok("Ami ajouté avec succès.");
        } catch (Exception e) {
            logger.error("Erreur lors de l'ajout d'ami : {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * ❌ Supprimer un ami de la liste des relations de l'utilisateur.
     */
    @DeleteMapping("/{userId}/removeFriend/{friendEmail}")
    public ResponseEntity<?> removeFriend(@PathVariable BigInteger userId, @PathVariable String friendEmail) {
        logger.info("Suppression d'un ami: utilisateur {} retire l'ami {}", userId, friendEmail);

        try {
            userService.removeFriend(userId, friendEmail);
            return ResponseEntity.ok("Ami supprimé avec succès.");
        } catch (Exception e) {
            logger.error("Erreur lors de la suppression d'ami : {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * 👥 Récupérer la liste des amis d'un utilisateur.
     */
    @GetMapping("/{userId}/friends")
    public ResponseEntity<?> getUserFriends(@PathVariable BigInteger userId) {
        logger.info("Récupération des amis de l'utilisateur avec ID: {}", userId);

        try {
            List<User> friends = userService.getUserFriends(userId);
            if (friends.isEmpty()) {
                return ResponseEntity.ok("Aucun ami trouvé.");
            }
            return ResponseEntity.ok(friends);
        } catch (Exception e) {
            logger.error("Erreur lors de la récupération des amis : {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * 🔄 Mettre à jour les informations de l'utilisateur (nom, email et mot de passe).
     */
    @PutMapping("/{userId}/update")
    public ResponseEntity<String> updateUser(
            @PathVariable BigInteger userId,
            @RequestParam(required = false) String newUsername,
            @RequestParam(required = false) String newEmail,
            @RequestParam(required = false) String newPassword) {

        try {
            userService.updateUser(userId, newUsername, newEmail);

            if (newPassword != null && !newPassword.isEmpty()) {
                userService.updatePassword(userId, newPassword);
            }

            return ResponseEntity.ok("Informations mises à jour avec succès.");
        } catch (IllegalArgumentException e) {
            logger.error("Erreur lors de la mise à jour : {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.error("Erreur serveur : {}", e.getMessage());
            return ResponseEntity.internalServerError().body("Une erreur est survenue.");
        }
    }
}
