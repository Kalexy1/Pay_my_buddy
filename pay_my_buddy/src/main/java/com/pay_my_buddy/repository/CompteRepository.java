package com.pay_my_buddy.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pay_my_buddy.model.Compte;
import com.pay_my_buddy.model.User;

import java.math.BigInteger;
import java.util.Optional;

/**
 * Interface de gestion des opérations de persistance pour l'entité {@link Compte}.
 * <p>
 * Fournit des méthodes pour accéder et manipuler les comptes stockés en base de données.
 * </p>
 */
public interface CompteRepository extends JpaRepository<Compte, BigInteger> {

    /**
     * Recherche un compte en fonction de l'utilisateur associé.
     *
     * @param user Utilisateur dont le compte est recherché.
     * @return Un {@link Optional} contenant le compte s'il existe, sinon un {@link Optional#empty()}.
     */
    Optional<Compte> findByUser(User user);
}
