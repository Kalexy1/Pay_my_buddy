package com.pay_my_buddy.repository;

import com.pay_my_buddy.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigInteger;
import java.util.Optional;

/**
 * Interface de gestion des opérations de persistance pour l'entité {@link User}.
 * <p>
 * Fournit des méthodes pour accéder et manipuler les utilisateurs stockés en base de données.
 * </p>
 */
public interface UserRepository extends JpaRepository<User, BigInteger> {

    /**
     * Recherche un utilisateur en fonction de son adresse email.
     *
     * @param email Adresse email de l'utilisateur recherché.
     * @return Un {@link Optional} contenant l'utilisateur s'il existe, sinon un {@link Optional#empty()}.
     */
    Optional<User> findByEmail(String email);
}
