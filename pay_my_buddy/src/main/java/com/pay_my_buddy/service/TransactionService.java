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

@Service
public class TransactionService {

    private static final Logger logger = LoggerFactory.getLogger(TransactionService.class);

    private final TransactionRepository transactionRepository;
    private final CompteService compteService;
    private final UserRepository userRepository;

    public TransactionService(TransactionRepository transactionRepository, CompteService compteService, UserRepository userRepository) {
        this.transactionRepository = transactionRepository;
        this.compteService = compteService;
        this.userRepository = userRepository;
    }

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
            throw e; // Re-throw specific exceptions to inform the caller
        } catch (Exception e) {
            logger.error("Erreur inattendue lors de la transaction entre {} et {}: {}", sender.getEmail(), receiver.getEmail(), e.getMessage());
            throw new RuntimeException("Une erreur est survenue lors de la transaction.");
        }
    }

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

    public List<Transaction> getUserTransactions(BigInteger userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé avec ID : " + userId));

        return transactionRepository.findBySenderOrReceiver(user, user);
    }

    /**
     * Récupérer l'historique des transactions d'un utilisateur (en tant qu'expéditeur ou destinataire).
     */
    public List<Transaction> getTransactionsForUser(User user) {
        return transactionRepository.findBySenderOrReceiver(user, user);
    }
}
