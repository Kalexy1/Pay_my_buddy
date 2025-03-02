package com.pay_my_buddy.controller;

import com.pay_my_buddy.model.Transaction;
import com.pay_my_buddy.model.User;
import com.pay_my_buddy.service.TransactionService;
import com.pay_my_buddy.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class TransactionControllerTest {

    @Mock
    private TransactionService transactionService;

    @Mock
    private UserService userService;

    @Mock
    private Authentication authentication;

    @Mock
    private Model model;

    @InjectMocks
    private TransactionController transactionController;

    private User user;
    private User receiver;
    private Transaction transaction;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setId(BigInteger.ONE);
        user.setEmail("test@example.com");

        receiver = new User();
        receiver.setId(BigInteger.TWO);
        receiver.setEmail("receiver@example.com");

        transaction = new Transaction();
        transaction.setSender(user);
        transaction.setReceiver(receiver);
        transaction.setAmount(BigDecimal.TEN);
    }

    @Test
    void testShowTransferPage() {
        when(authentication.getName()).thenReturn(user.getEmail());
        when(userService.getUserByEmail(user.getEmail())).thenReturn(user);
        when(transactionService.getTransactionsForUser(user)).thenReturn(Collections.singletonList(transaction));
        when(userService.getUserFriends(user.getId())).thenReturn(Collections.emptyList());

        String view = transactionController.showTransferPage(authentication, model, null, null);

        assertEquals("transfer", view);
        verify(model).addAttribute(eq("user"), any(User.class));
        verify(model).addAttribute(eq("transactions"), anyList());
        verify(model).addAttribute(eq("friends"), anyList());
    }

    @Test
    void testSendMoney_Success() {
        when(userService.getUserById(BigInteger.ONE)).thenReturn(user);
        when(userService.getUserByEmail("receiver@example.com")).thenReturn(receiver);

        String view = transactionController.sendMoney(BigInteger.ONE, "receiver@example.com", BigDecimal.TEN, "Test Transaction");

        assertEquals("redirect:/transfer?success=Transaction réussie !", view);
        verify(transactionService).sendMoney(user, receiver, BigDecimal.TEN, "Test Transaction");
    }

    @Test
    void testSendMoney_Failure_InsufficientFunds() {
        when(userService.getUserById(BigInteger.ONE)).thenReturn(user);
        when(userService.getUserByEmail("receiver@example.com")).thenReturn(receiver);
        doThrow(new IllegalArgumentException("Fonds insuffisants pour effectuer cette transaction."))
                .when(transactionService).sendMoney(user, receiver, BigDecimal.TEN, "Test Transaction");

        String view = transactionController.sendMoney(BigInteger.ONE, "receiver@example.com", BigDecimal.TEN, "Test Transaction");

        assertEquals("redirect:/transfer?error=Fonds insuffisants pour effectuer cette transaction.", view);
    }

    @Test
    void testSendMoney_Failure_InvalidAmount() {
        String view = transactionController.sendMoney(BigInteger.ONE, "receiver@example.com", BigDecimal.ZERO, "Test Transaction");

        assertEquals("redirect:/transfer?error=Montant invalide.", view);
    }
}
