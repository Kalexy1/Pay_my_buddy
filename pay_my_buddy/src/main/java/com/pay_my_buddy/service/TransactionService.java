package com.pay_my_buddy.service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pay_my_buddy.exception.ResourceNotFoundException;
import com.pay_my_buddy.model.Transaction;
import com.pay_my_buddy.model.User;
import com.pay_my_buddy.repository.TransactionRepository;
import com.pay_my_buddy.repository.UserRepository;

/**
 * Service gérant les transactions financières entre utilisateurs.
 * <p>
 * Permet d'effectuer des paiements entre utilisateurs et de récupérer l'historique des transactions.
 * </p>
 */
@Service
public class TransactionService {

    /**
     * Logger pour suivre les opérations de transaction.
     */
    private static final Logger logger = LoggerFactory.getLogger(TransactionService.class);

    /**
     * Référentiel des transactions.
     */
    private final TransactionRepository transactionRepository;

    /**
     * Service de gestion des comptes utilisateurs.
     */
    private final CompteService compteService;

    /**
     * Référentiel des utilisateurs.
     */
    private final UserRepository userRepository;

    /**
     * Constructeur du service {@code TransactionService}.
     *
     * @param transactionRepository Référentiel des transactions.
     * @param compteService Service de gestion des comptes.
     * @param userRepository Référentiel des utilisateurs.
     */
    public TransactionService(TransactionRepository transactionRepository, CompteService compteService, UserRepository userRepository) {
        this.transactionRepository = transactionRepository;
        this.compteService = compteService;
        this.userRepository = userRepository;
    }

    /**
     * Effectue un transfert d'argent entre deux utilisateurs.
     *
     * @param sender Expéditeur de la transaction.
     * @param receiver Destinataire de la transaction.
     * @param amount Montant à transférer.
     * @param description Description de la transaction.
     * @return La transaction créée.
     * @throws IllegalArgumentException Si l'expéditeur et le destinataire sont identiques ou si les fonds sont insuffisants.
     * @throws RuntimeException En cas d'erreur inattendue.
     */
    @Transactional
    public Transaction sendMoney(User sender, User receiver, BigDecimal amount, String description) {
        try {
            if (sender.equals(receiver)) {
                throw new IllegalArgumentException("Impossible d'envoyer de l'argent à soi-même.");
            }

            BigDecimal totalAmount = amount;

            if (!compteService.hasSufficientFunds(sender, totalAmount)) {
                throw new IllegalArgumentException("Fonds insuffisants pour effectuer cette transaction.");
            }

            compteService.debitCompte(sender, totalAmount);
            compteService.creditCompte(receiver, amount);

            Transaction transaction = new Transaction();
            transaction.setSender(sender);
            transaction.setReceiver(receiver);
            transaction.setAmount(amount);
            transaction.setDescription(description);
            transactionRepository.save(transaction);

            logger.info("Transaction réussie de {} EUR de {} vers {}", amount, sender.getEmail(), receiver.getEmail());
            return transaction;
        } catch (IllegalArgumentException e) {
            logger.warn("Échec de la transaction entre {} et {}: {}", sender.getEmail(), receiver.getEmail(), e.getMessage());
            throw e; 
        } catch (Exception e) {
            logger.error("Erreur inattendue lors de la transaction entre {} et {}: {}", sender.getEmail(), receiver.getEmail(), e.getMessage());
            throw new RuntimeException("Une erreur est survenue lors de la transaction.");
        }
    }

    /**
     * Crée une transaction entre deux utilisateurs.
     *
     * @param sender Expéditeur de la transaction.
     * @param receiver Destinataire de la transaction.
     * @param description Description de la transaction.
     * @param amount Montant de la transaction.
     * @return La transaction créée.
     * @throws IllegalArgumentException Si l'expéditeur et le destinataire sont identiques ou si les fonds sont insuffisants.
     */
    public Transaction createTransaction(User sender, User receiver, String description, double amount) {
        if (sender.equals(receiver)) {
            throw new IllegalArgumentException("Impossible d'envoyer de l'argent à soi-même.");
        }

        BigDecimal totalAmount = BigDecimal.valueOf(amount);

        if (!compteService.hasSufficientFunds(sender, totalAmount)) {
            throw new IllegalArgumentException("Fonds insuffisants pour effectuer cette transaction.");
        }

        compteService.debitCompte(sender, totalAmount);
        compteService.creditCompte(receiver, BigDecimal.valueOf(amount));

        Transaction transaction = new Transaction();
        transaction.setSender(sender);
        transaction.setReceiver(receiver);
        transaction.setAmount(BigDecimal.valueOf(amount));
        transaction.setDescription(description);
        transactionRepository.save(transaction);

        logger.info("Transaction réussie de {} EUR de {} vers {}", amount, sender.getEmail(), receiver.getEmail());
        return transaction;
    }

    /**
     * Récupère l'historique des transactions d'un utilisateur.
     *
     * @param userId Identifiant de l'utilisateur.
     * @return Liste des transactions impliquant l'utilisateur.
     * @throws ResourceNotFoundException Si l'utilisateur est introuvable.
     */
    public List<Transaction> getUserTransactions(BigInteger userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé avec ID : " + userId));

        return transactionRepository.findBySenderOrReceiver(user, user);
    }

    /**
     * Récupère l'historique des transactions d'un utilisateur (en tant qu'expéditeur ou destinataire).
     *
     * @param user Utilisateur dont on veut récupérer les transactions.
     * @return Liste des transactions associées à l'utilisateur.
     */
    public List<Transaction> getTransactionsForUser(User user) {
        return transactionRepository.findBySenderOrReceiver(user, user);
    }
}
