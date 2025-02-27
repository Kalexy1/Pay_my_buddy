package com.pay_my_buddy.security;

import com.pay_my_buddy.model.User;
import com.pay_my_buddy.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service personnalisé pour la gestion de l'authentification des utilisateurs.
 * <p>
 * Implémente l'interface {@link UserDetailsService} de Spring Security afin de charger les détails
 * d'un utilisateur à partir de son adresse email.
 * </p>
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    /**
     * Logger pour suivre les tentatives d'authentification.
     */
    private static final Logger logger = LoggerFactory.getLogger(CustomUserDetailsService.class);

    /**
     * Référentiel des utilisateurs pour récupérer les informations d'authentification.
     */
    private final UserRepository userRepository;

    /**
     * Constructeur de la classe {@code CustomUserDetailsService}.
     *
     * @param userRepository Référentiel des utilisateurs pour la gestion de l'authentification.
     */
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Charge un utilisateur par son adresse email.
     *
     * @param email L'adresse email de l'utilisateur.
     * @return Un objet {@link UserDetails} contenant les informations de l'utilisateur.
     * @throws UsernameNotFoundException Si aucun utilisateur correspondant n'est trouvé.
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        if (email == null || email.trim().isEmpty()) {
            logger.warn("Tentative de connexion avec un e-mail vide !");
            throw new UsernameNotFoundException("L'e-mail ne peut pas être vide");
        }

        logger.info("Recherche de l'utilisateur: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.warn("Aucun utilisateur trouvé pour : {}", email);
                    return new UsernameNotFoundException("Email introuvable");
                });

        logger.info("Utilisateur trouvé : {}", user.getEmail());

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                Collections.singleton(new SimpleGrantedAuthority("USER"))
        );
    }
}
