package com.pay_my_buddy.service;

import com.pay_my_buddy.model.Compte;
import com.pay_my_buddy.model.User;
import com.pay_my_buddy.repository.CompteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigInteger;

@Service
public class CompteService {
    private static final Logger logger = LoggerFactory.getLogger(CompteService.class);
    private final CompteRepository compteRepository;

    public CompteService(CompteRepository compteRepository) {
        this.compteRepository = compteRepository;
    }

    /**
     * Créer un compte pour un nouvel utilisateur avec un solde initial de 0.
     */
    @Transactional
    public Compte createCompteForUser(User user) {
        if (compteRepository.findByUser(user).isPresent()) {
            logger.warn("Un compte existe déjà pour {}", user.getEmail());
            return compteRepository.findByUser(user).get();
        }

        Compte compte = new Compte(user);
        compte.setSolde(BigDecimal.ZERO);
        Compte savedCompte = compteRepository.save(compte);

        logger.info("Compte bancaire créé pour {}", user.getEmail());
        return savedCompte;
    }

    /**
     * Récupérer le compte d'un utilisateur.
     */
    public Compte getCompteByUser(User user) {
        return compteRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Compte utilisateur introuvable"));
    }

    /**
     * Créditer le compte de l'utilisateur.
     *
     * @param user   L'utilisateur.
     * @param amount Montant à créditer.
     */
    @Transactional
    public void creditCompte(User user, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            logger.warn("Tentative de crédit d'un montant non valide ({}) pour {}", amount, user.getEmail());
            throw new IllegalArgumentException("Le montant à créditer doit être supérieur à zéro.");
        }

        Compte compte = getCompteByUser(user);
        compte.setSolde(compte.getSolde().add(amount));
        compteRepository.save(compte);

        logger.info("Crédit de {} EUR ajouté au compte de {}", amount, user.getEmail());
    }

    /**
     * Débiter le compte de l'utilisateur.
     *
     * @param user   L'utilisateur.
     * @param amount Montant à débiter.
     */
    @Transactional
    public void debitCompte(User user, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            logger.warn("Tentative de débit d'un montant non valide ({}) pour {}", amount, user.getEmail());
            throw new IllegalArgumentException("Le montant à débiter doit être supérieur à zéro.");
        }

        Compte compte = getCompteByUser(user);

        if (compte.getSolde().compareTo(amount) < 0) {
            logger.warn("Solde insuffisant pour débiter {} EUR du compte de {}", amount, user.getEmail());
            throw new RuntimeException("Solde insuffisant");
        }

        compte.setSolde(compte.getSolde().subtract(amount));
        compteRepository.save(compte);

        logger.info("Débit de {} EUR effectué sur le compte de {}", amount, user.getEmail());
    }

    /**
     * Vérifie si l'utilisateur a suffisamment de fonds.
     *
     * @param user   L'utilisateur.
     * @param amount Montant à vérifier.
     * @return true si le solde est suffisant, sinon false.
     */
    public boolean hasSufficientFunds(User user, BigDecimal amount) {
        Compte compte = getCompteByUser(user);
        boolean hasFunds = compte.getSolde().compareTo(amount) >= 0;

        if (!hasFunds) {
            logger.warn("Fonds insuffisants : {} tente de transférer {} EUR alors que son solde est de {} EUR",
                    user.getEmail(), amount, compte.getSolde());
        }

        return hasFunds;
    }
}
