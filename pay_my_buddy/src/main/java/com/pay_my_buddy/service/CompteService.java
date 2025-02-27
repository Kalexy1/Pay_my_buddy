package com.pay_my_buddy.service;

import com.pay_my_buddy.model.Compte;
import com.pay_my_buddy.model.User;
import com.pay_my_buddy.repository.CompteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * Service gérant les opérations liées aux comptes des utilisateurs.
 * <p>
 * Permet la création, la consultation, le crédit et le débit des comptes.
 * </p>
 */
@Service
public class CompteService {

    /**
     * Logger pour suivre les opérations effectuées sur les comptes.
     */
    private static final Logger logger = LoggerFactory.getLogger(CompteService.class);

    /**
     * Référentiel pour la gestion des comptes en base de données.
     */
    private final CompteRepository compteRepository;

    /**
     * Constructeur du service {@code CompteService}.
     *
     * @param compteRepository Référentiel des comptes pour la gestion des opérations bancaires.
     */
    public CompteService(CompteRepository compteRepository) {
        this.compteRepository = compteRepository;
    }

    /**
     * Crée un compte pour un nouvel utilisateur avec un solde initial de 0.
     *
     * @param user L'utilisateur pour lequel le compte est créé.
     * @return Le compte créé ou existant.
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
     * Récupère le compte d'un utilisateur.
     *
     * @param user L'utilisateur dont le compte est recherché.
     * @return Le compte de l'utilisateur.
     * @throws RuntimeException Si le compte est introuvable.
     */
    public Compte getCompteByUser(User user) {
        return compteRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Compte utilisateur introuvable"));
    }

    /**
     * Créditer le compte de l'utilisateur d'un montant donné.
     *
     * @param user   L'utilisateur concerné.
     * @param amount Montant à créditer.
     * @throws IllegalArgumentException Si le montant est inférieur ou égal à zéro.
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
     * Débite le compte de l'utilisateur d'un montant donné.
     *
     * @param user   L'utilisateur concerné.
     * @param amount Montant à débiter.
     * @throws IllegalArgumentException Si le montant est inférieur ou égal à zéro.
     * @throws RuntimeException         Si le solde est insuffisant.
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
     * Vérifie si l'utilisateur a suffisamment de fonds pour effectuer une transaction.
     *
     * @param user   L'utilisateur concerné.
     * @param amount Montant à vérifier.
     * @return {@code true} si le solde est suffisant, sinon {@code false}.
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
