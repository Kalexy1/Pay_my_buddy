package com.pay_my_buddy.controller;

import com.pay_my_buddy.model.Transaction;
import com.pay_my_buddy.model.User;
import com.pay_my_buddy.service.TransactionService;
import com.pay_my_buddy.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

/**
 * Contrôleur gérant les transactions entre utilisateurs.
 * <p>
 * Cette classe permet d'afficher la page de transfert et d'effectuer des transactions.
 * </p>
 */
@Controller
public class TransactionController {

    private final TransactionService transactionService;
    private final UserService userService;

    /**
     * Constructeur de la classe TransactionController.
     *
     * @param transactionService Service de gestion des transactions.
     * @param userService        Service de gestion des utilisateurs.
     */
    public TransactionController(TransactionService transactionService, UserService userService) {
        this.transactionService = transactionService;
        this.userService = userService;
    }

    /**
     * Affiche la page de transfert avec l'historique des transactions et la liste des amis.
     *
     * @param authentication Informations d'authentification de l'utilisateur connecté.
     * @param model          Modèle pour transmettre des attributs à la vue.
     * @param success        Message facultatif indiquant un succès.
     * @param error          Message facultatif indiquant une erreur.
     * @return La vue de la page de transfert.
     */
    @GetMapping("/transfer")
    public String showTransferPage(Authentication authentication, Model model,
                                   @RequestParam(value = "success", required = false) String success,
                                   @RequestParam(value = "error", required = false) String error) {
        String email = authentication.getName();
        User user = userService.getUserByEmail(email);
        List<Transaction> transactions = transactionService.getTransactionsForUser(user);
        List<User> friends = userService.getUserFriends(user.getId());

        model.addAttribute("user", user);
        model.addAttribute("transactions", transactions);
        model.addAttribute("friends", friends);

        if (success != null) {
            model.addAttribute("success", success);
        }
        if (error != null) {
            model.addAttribute("error", error);
        }

        return "transfer";
    }

    /**
     * Effectue un transfert d'argent entre l'utilisateur et l'un de ses amis.
     *
     * @param senderId    Identifiant de l'expéditeur.
     * @param friendEmail Email du destinataire.
     * @param amount      Montant à transférer.
     * @param description Description de la transaction.
     * @return Redirection vers la page de transfert avec un message de succès ou d'erreur.
     */
    @PostMapping("/transfer")
    public String sendMoney(@RequestParam BigInteger senderId,
                            @RequestParam String friendEmail,
                            @RequestParam BigDecimal amount,
                            @RequestParam String description) {
        User sender = userService.getUserById(senderId);
        User receiver = userService.getUserByEmail(friendEmail);

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            return "redirect:/transfer?error=Montant invalide.";
        }

        try {
            transactionService.sendMoney(sender, receiver, amount, description);
            return "redirect:/transfer?success=Transaction réussie !";
        } catch (IllegalArgumentException e) {
            return "redirect:/transfer?error=" + e.getMessage();
        }
    }
}
