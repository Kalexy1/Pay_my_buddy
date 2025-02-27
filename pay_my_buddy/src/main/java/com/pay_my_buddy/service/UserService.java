package com.pay_my_buddy.service;

import com.pay_my_buddy.exception.ResourceNotFoundException;
import com.pay_my_buddy.model.Compte;
import com.pay_my_buddy.model.User;
import com.pay_my_buddy.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.List;

@Service
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CompteService compteService;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, CompteService compteService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.compteService = compteService;
    }

    /**
     * 🔒 Enregistre un nouvel utilisateur avec un mot de passe crypté et crée son compte bancaire.
     */
    @Transactional
    public User registerUser(User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Cet email est déjà utilisé.");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Sauvegarde de l'utilisateur
        User savedUser = userRepository.save(user);
        userRepository.flush(); // 🔥 Force l'exécution immédiate de la requête SQL

        // ✅ Création du compte bancaire
        Compte compte = compteService.createCompteForUser(savedUser);
        
        if (compte == null) {
            throw new RuntimeException("Erreur lors de la création du compte bancaire.");
        }

        logger.info("Utilisateur {} enregistré avec succès et compte bancaire créé.", savedUser.getEmail());
        return savedUser;
    }

    /**
     * 🔑 Authentifie un utilisateur avec son email et son mot de passe.
     */
    public User authenticate(String email, String password) {
        logger.info("Tentative de connexion pour l'email : {}", email);

        User user = getUserByEmail(email);

        if (!passwordEncoder.matches(password, user.getPassword())) {
            logger.warn("Échec d'authentification : mot de passe incorrect pour {}", email);
            throw new IllegalArgumentException("Email ou mot de passe incorrect.");
        }

        logger.info("Connexion réussie pour : {}", email);
        return user;
    }

    /**
     * 📧 Récupère un utilisateur par son email.
     */
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.error("Utilisateur introuvable avec l'email : {}", email);
                    return new ResourceNotFoundException("Utilisateur non trouvé avec l'email : " + email);
                });
    }

    /**
     * 🔎 Récupère un utilisateur par son ID.
     */
    public User getUserById(BigInteger id) {
        return userRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Utilisateur introuvable avec l'ID : {}", id);
                    return new ResourceNotFoundException("Utilisateur non trouvé avec l'ID : " + id);
                });
    }

    /**
     * ➕ Ajoute un ami à la liste de connexions d'un utilisateur.
     */
    @Transactional
    public void addFriend(BigInteger userId, String friendEmail) {
        User user = getUserById(userId);
        User friend = getUserByEmail(friendEmail);

        if (user.equals(friend)) {
            throw new IllegalArgumentException("Vous ne pouvez pas vous ajouter vous-même.");
        }
        if (user.getFriends().contains(friend)) {
            throw new IllegalArgumentException("Cet utilisateur est déjà dans votre liste d'amis.");
        }

        user.getFriends().add(friend);
        friend.getFriends().add(user);

        userRepository.save(user);
        userRepository.save(friend);
        logger.info("{} a ajouté {} comme ami", user.getEmail(), friend.getEmail());
    }

    /**
     * ❌ Supprime un ami de la liste de connexions d'un utilisateur.
     */
    @Transactional
    public void removeFriend(BigInteger userId, String friendEmail) {
        User user = getUserById(userId);
        User friend = getUserByEmail(friendEmail);

        if (!user.getFriends().contains(friend)) {
            throw new IllegalArgumentException("Cet utilisateur n'est pas dans votre liste d'amis.");
        }

        user.getFriends().remove(friend);
        friend.getFriends().remove(user);

        userRepository.save(user);
        userRepository.save(friend);
        logger.info("{} a supprimé {} de sa liste d'amis", user.getEmail(), friend.getEmail());
    }

    /**
     * 👥 Récupère la liste des amis d'un utilisateur.
     */
    public List<User> getUserFriends(BigInteger userId) {
        User user = getUserById(userId);
        return List.copyOf(user.getFriends());
    }

    /**
     * ✏️ Met à jour les informations d'un utilisateur.
     */
    @Transactional
    public void updateUser(BigInteger userId, String newUsername, String newEmail) {
        User user = getUserById(userId);

        if (newEmail != null && !newEmail.equals(user.getEmail())) {
            if (userRepository.findByEmail(newEmail).isPresent()) {
                throw new IllegalArgumentException("Cet email est déjà utilisé.");
            }
            user.setEmail(newEmail);
        }

        if (newUsername != null && !newUsername.isEmpty()) {
            user.setUsername(newUsername);
        }

        userRepository.save(user);
    }

    /**
     * 🔑 Met à jour le mot de passe d'un utilisateur.
     */
    @Transactional
    public void updatePassword(BigInteger userId, String newPassword) {
        User user = getUserById(userId);

        if (newPassword.length() < 6) {
            throw new IllegalArgumentException("Le mot de passe doit contenir au moins 6 caractères.");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
}
