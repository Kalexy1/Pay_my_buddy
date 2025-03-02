package com.pay_my_buddy.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.pay_my_buddy.exception.UserNotFoundException;
import com.pay_my_buddy.model.User;

import java.math.BigInteger;

/**
 * Service gérant les connexions entre utilisateurs.
 * <p>
 * Permet l'ajout d'amis entre les utilisateurs de l'application.
 * </p>
 */
@Service
public class ConnectionService {

    /**
     * Logger pour suivre les opérations liées aux connexions entre utilisateurs.
     */
    private static final Logger logger = LoggerFactory.getLogger(ConnectionService.class);

    /**
     * Service pour la gestion des utilisateurs.
     */
    private final UserService userService;

    /**
     * Constructeur du service {@code ConnectionService}.
     *
     * @param userService Service des utilisateurs pour la gestion des connexions.
     */
    public ConnectionService(UserService userService) {
        this.userService = userService;
    }

    /**
     * Ajoute une connexion entre deux utilisateurs en les liant comme amis.
     *
     * @param userId   Identifiant de l'utilisateur.
     * @param friendId Identifiant de l'ami à ajouter.
     * @throws UserNotFoundException Si l'un des utilisateurs n'est pas trouvé.
     */
    public void addConnection(BigInteger userId, BigInteger friendId) {
        User user = userService.getUserById(userId);
        User friend = userService.getUserById(friendId);

        if (user.getFriends().contains(friend)) {
            logger.warn("Les utilisateurs {} et {} sont déjà amis", userId, friendId);
            return;
        }

        user.getFriends().add(friend);
        friend.getFriends().add(user);

        userService.saveUser(user);
        userService.saveUser(friend);

        logger.info("L'utilisateur {} a ajouté {} comme ami.", user.getEmail(), friend.getEmail());
    }
}
