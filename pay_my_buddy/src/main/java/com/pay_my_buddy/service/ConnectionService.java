package com.pay_my_buddy.service;

import org.springframework.stereotype.Service;

import com.pay_my_buddy.model.User;
import com.pay_my_buddy.repository.UserRepository;

import java.math.BigInteger;
import java.util.Optional;

/**
 * Service gérant les connexions entre utilisateurs.
 * <p>
 * Permet l'ajout d'amis entre les utilisateurs de l'application.
 * </p>
 */
@Service
public class ConnectionService {

    /**
     * Référentiel pour la gestion des utilisateurs en base de données.
     */
    private final UserRepository userRepository;

    /**
     * Constructeur du service {@code ConnectionService}.
     *
     * @param userRepository Référentiel des utilisateurs pour la gestion des connexions.
     */
    public ConnectionService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Ajoute une connexion entre deux utilisateurs en les liant comme amis.
     *
     * @param userId   Identifiant de l'utilisateur.
     * @param friendId Identifiant de l'ami à ajouter.
     * @throws RuntimeException Si l'un des utilisateurs n'est pas trouvé.
     */
    public void addConnection(BigInteger userId, BigInteger friendId) {
        Optional<User> userOpt = userRepository.findById(userId);
        Optional<User> friendOpt = userRepository.findById(friendId);

        if (userOpt.isPresent() && friendOpt.isPresent()) {
            User user = userOpt.get();
            User friend = friendOpt.get();
            user.getFriends().add(friend);
            userRepository.save(user);
        } else {
            throw new RuntimeException("Utilisateur ou ami non trouvé");
        }
    }
}
