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

/**
 * Service gérant les opérations liées aux utilisateurs.
 * <p>
 * Permet l'enregistrement, l'authentification, la gestion des amis et la mise à jour des informations utilisateur.
 * </p>
 */
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
     * Enregistre un nouvel utilisateur avec un mot de passe crypté et crée son compte bancaire.
     *
     * @param user L'utilisateur à enregistrer.
     * @return L'utilisateur enregistré.
     * @throws IllegalArgumentException Si l'email est déjà utilisé.
     * @throws RuntimeException Si la création du compte bancaire échoue.
     */
    @Transactional
    public User registerUser(User user) {
        checkIfEmailExists(user.getEmail());

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userRepository.save(user);
        userRepository.flush();

        Compte compte = compteService.createCompteForUser(savedUser);
        if (compte == null) {
            throw new RuntimeException("Erreur lors de la création du compte bancaire.");
        }

        logger.info("Utilisateur {} enregistré avec succès et compte bancaire créé.", savedUser.getEmail());
        return savedUser;
    }

    /**
     * Authentifie un utilisateur avec son email et son mot de passe.
     *
     * @param email Email de l'utilisateur.
     * @param password Mot de passe de l'utilisateur.
     * @return L'utilisateur authentifié.
     * @throws IllegalArgumentException Si les identifiants sont incorrects.
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
     * Récupère un utilisateur par son email.
     *
     * @param email Email de l'utilisateur.
     * @return L'utilisateur correspondant.
     * @throws ResourceNotFoundException Si l'utilisateur est introuvable.
     */
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé avec l'email : " + email));
    }

    /**
     * Récupère un utilisateur par son ID.
     *
     * @param id Identifiant de l'utilisateur.
     * @return L'utilisateur correspondant.
     * @throws ResourceNotFoundException Si l'utilisateur est introuvable.
     */
    public User getUserById(BigInteger id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé avec l'ID : " + id));
    }

    /**
     * Ajoute un ami à la liste de connexions d'un utilisateur.
     *
     * @param userId ID de l'utilisateur.
     * @param friendEmail Email de l'ami à ajouter.
     * @throws IllegalArgumentException Si l'utilisateur tente de s'ajouter lui-même ou si l'ami est déjà présent.
     */
    @Transactional
    public void addFriend(BigInteger userId, String friendEmail) {
        User user = getUserById(userId);
        User friend = getUserByEmail(friendEmail);

        checkIfAlreadyFriends(user, friend);

        user.getFriends().add(friend);
        friend.getFriends().add(user);

        userRepository.save(user);
        userRepository.save(friend);
        logger.info("{} a ajouté {} comme ami", user.getEmail(), friend.getEmail());
    }

    /**
     * Supprime un ami de la liste de connexions d'un utilisateur.
     *
     * @param userId ID de l'utilisateur.
     * @param friendEmail Email de l'ami à supprimer.
     * @throws IllegalArgumentException Si l'utilisateur ne fait pas partie de la liste d'amis.
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
     * Récupère la liste des amis d'un utilisateur.
     *
     * @param userId ID de l'utilisateur.
     * @return Liste des amis de l'utilisateur.
     */
    public List<User> getUserFriends(BigInteger userId) {
        User user = getUserById(userId);
        return List.copyOf(user.getFriends());
    }

    /**
     * Met à jour les informations d'un utilisateur.
     *
     * @param userId ID de l'utilisateur.
     * @param newUsername Nouveau nom d'utilisateur (optionnel).
     * @param newEmail Nouvel email (optionnel).
     * @throws IllegalArgumentException Si l'email est déjà utilisé.
     */
    @Transactional
    public void updateUser(BigInteger userId, String newUsername, String newEmail) {
        User user = getUserById(userId);

        if (newEmail != null && !newEmail.equals(user.getEmail())) {
            checkIfEmailExists(newEmail);
            user.setEmail(newEmail);
        }

        if (newUsername != null && !newUsername.isEmpty()) {
            user.setUsername(newUsername);
        }

        userRepository.save(user);
    }

    /**
     * Met à jour le mot de passe d'un utilisateur.
     *
     * @param userId ID de l'utilisateur.
     * @param newPassword Nouveau mot de passe.
     * @throws IllegalArgumentException Si le mot de passe est trop court.
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

    /**
     * Vérifie si un email est déjà utilisé dans la base de données.
     *
     * @param email Email à vérifier.
     * @throws IllegalArgumentException Si l'email est déjà enregistré.
     */
    private void checkIfEmailExists(String email) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Cet email est déjà utilisé.");
        }
    }

    /**
     * Vérifie si deux utilisateurs sont déjà amis ou si l'utilisateur tente de s'ajouter lui-même.
     *
     * @param user   L'utilisateur qui veut ajouter un ami.
     * @param friend L'ami potentiel à ajouter.
     * @throws IllegalArgumentException Si l'utilisateur tente de s'ajouter lui-même ou si l'ami est déjà présent.
     */
    private void checkIfAlreadyFriends(User user, User friend) {
        if (user.equals(friend)) {
            throw new IllegalArgumentException("Vous ne pouvez pas vous ajouter vous-même.");
        }
        if (user.getFriends().contains(friend)) {
            throw new IllegalArgumentException("Cet utilisateur est déjà dans votre liste d'amis.");
        }
    }
    
    /**
     * Saves the user to the database.
     *
     * @param user the user to be saved.
     * @return the saved user.
     */
    public User saveUser(User user) {
        return userRepository.save(user);
    }

}
