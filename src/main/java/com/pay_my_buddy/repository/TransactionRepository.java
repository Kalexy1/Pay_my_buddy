package com.pay_my_buddy.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pay_my_buddy.model.Transaction;
import com.pay_my_buddy.model.User;

import java.math.BigInteger;
import java.util.List;

/**
 * Interface de gestion des opérations de persistance pour l'entité {@link Transaction}.
 * <p>
 * Fournit des méthodes pour accéder et manipuler les transactions stockées en base de données.
 * </p>
 */
public interface TransactionRepository extends JpaRepository<Transaction, BigInteger> {

    /**
     * Recherche les transactions associées à un utilisateur en tant qu'expéditeur ou destinataire.
     *
     * @param sender   Utilisateur ayant envoyé la transaction.
     * @param receiver Utilisateur ayant reçu la transaction.
     * @return Une liste de transactions impliquant l'utilisateur donné en tant qu'expéditeur ou destinataire.
     */
    List<Transaction> findBySenderOrReceiver(User sender, User receiver);
}
