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

/**
 * Contrôleur REST pour la gestion des utilisateurs.
 * <p>
 * Cette classe fournit des endpoints pour récupérer, mettre à jour et gérer les relations d'amitié entre utilisateurs.
 * </p>
 */
@RestController
@RequestMapping("/users")
@Validated
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    /**
     * Constructeur de la classe UserController.
     *
     * @param userService Service de gestion des utilisateurs.
     */
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Récupère un utilisateur par son email.
     *
     * @param email Email de l'utilisateur recherché.
     * @return {@link ResponseEntity} contenant l'utilisateur ou un message d'erreur si non trouvé.
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
     * Ajoute un ami à la liste des amis de l'utilisateur.
     *
     * @param userId      Identifiant de l'utilisateur.
     * @param friendEmail Email de l'ami à ajouter.
     * @return {@link ResponseEntity} contenant un message de succès ou d'erreur.
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
     * Supprime un ami de la liste des relations de l'utilisateur.
     *
     * @param userId      Identifiant de l'utilisateur.
     * @param friendEmail Email de l'ami à supprimer.
     * @return {@link ResponseEntity} contenant un message de succès ou d'erreur.
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
     * Récupère la liste des amis d'un utilisateur.
     *
     * @param userId Identifiant de l'utilisateur.
     * @return {@link ResponseEntity} contenant la liste des amis ou un message si aucun ami trouvé.
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
     * Met à jour les informations de l'utilisateur (nom, email et mot de passe).
     *
     * @param userId      Identifiant de l'utilisateur.
     * @param newUsername Nouveau nom d'utilisateur (optionnel).
     * @param newEmail    Nouveau email de l'utilisateur (optionnel).
     * @param newPassword Nouveau mot de passe (optionnel).
     * @return {@link ResponseEntity} contenant un message de succès ou d'erreur.
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
