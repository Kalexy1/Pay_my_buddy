package com.pay_my_buddy.service;

import com.pay_my_buddy.exception.ResourceNotFoundException;
import com.pay_my_buddy.model.Transaction;
import com.pay_my_buddy.model.User;
import com.pay_my_buddy.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private CompteService compteService;

    @Mock
    private UserService userService;

    @InjectMocks
    private TransactionService transactionService;

    private User sender;
    private User receiver;
    private Transaction transaction;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        sender = new User();
        sender.setId(BigInteger.ONE);
        sender.setEmail("sender@example.com");

        receiver = new User();
        receiver.setId(BigInteger.TWO);
        receiver.setEmail("receiver@example.com");

        transaction = new Transaction();
        transaction.setSender(sender);
        transaction.setReceiver(receiver);
        transaction.setAmount(BigDecimal.TEN);
        transaction.setDescription("Test Transaction");
    }

    @Test
    void testSendMoney_Success() {
        when(userService.getUserById(sender.getId())).thenReturn(sender);
        when(userService.getUserById(receiver.getId())).thenReturn(receiver);
        when(compteService.hasSufficientFunds(sender, BigDecimal.TEN)).thenReturn(true);
        doNothing().when(compteService).debitCompte(sender, BigDecimal.TEN);
        doNothing().when(compteService).creditCompte(receiver, BigDecimal.TEN);

        ArgumentCaptor<Transaction> captor = ArgumentCaptor.forClass(Transaction.class);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);

        Transaction result = transactionService.sendMoney(sender, receiver, BigDecimal.TEN, "Test Transaction");

        assertNotNull(result);
        assertEquals(BigDecimal.TEN, result.getAmount());

        verify(transactionRepository).save(captor.capture());
        Transaction capturedTransaction = captor.getValue();
        assertEquals("Test Transaction", capturedTransaction.getDescription());
        assertEquals(sender, capturedTransaction.getSender());
        assertEquals(receiver, capturedTransaction.getReceiver());
    }

    @Test
    void testSendMoney_InsufficientFunds() {
        when(userService.getUserById(sender.getId())).thenReturn(sender);
        when(userService.getUserById(receiver.getId())).thenReturn(receiver);
        when(compteService.hasSufficientFunds(sender, BigDecimal.TEN)).thenReturn(false);

        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> transactionService.sendMoney(sender, receiver, BigDecimal.TEN, "Test Transaction"));

        assertEquals("Fonds insuffisants pour effectuer cette transaction.", exception.getMessage());
    }

    @Test
    void testSendMoney_SelfTransfer() {
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> transactionService.sendMoney(sender, sender, BigDecimal.TEN, "Self Transfer"));

        assertEquals("Impossible d'envoyer de l'argent à soi-même.", exception.getMessage());
    }

    @Test
    void testGetUserTransactions_Success() {
        when(userService.getUserById(sender.getId())).thenReturn(sender);
        when(transactionRepository.findBySenderOrReceiver(sender, sender))
                .thenReturn(Collections.singletonList(transaction));

        List<Transaction> transactions = transactionService.getUserTransactions(sender.getId());

        assertFalse(transactions.isEmpty());
        assertEquals(1, transactions.size());
        assertEquals(transaction, transactions.get(0));
    }

    @Test
    void testCreateTransaction_Success() {
        BigDecimal amount = BigDecimal.valueOf(100.00);
        String description = "Paiement test";

        when(userService.getUserById(sender.getId())).thenReturn(sender);
        when(userService.getUserById(receiver.getId())).thenReturn(receiver);
        when(compteService.hasSufficientFunds(sender, amount)).thenReturn(true);
        doNothing().when(compteService).debitCompte(sender, amount);
        doNothing().when(compteService).creditCompte(receiver, amount);

        ArgumentCaptor<Transaction> captor = ArgumentCaptor.forClass(Transaction.class);
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Transaction createdTransaction = transactionService.createTransaction(sender, receiver, description, amount);

        assertNotNull(createdTransaction);
        assertEquals(sender, createdTransaction.getSender());
        assertEquals(receiver, createdTransaction.getReceiver());
        assertEquals(amount, createdTransaction.getAmount());
        assertEquals(description, createdTransaction.getDescription());

        verify(compteService, times(1)).debitCompte(sender, amount);
        verify(compteService, times(1)).creditCompte(receiver, amount);
        verify(transactionRepository, times(1)).save(captor.capture());

        Transaction capturedTransaction = captor.getValue();
        assertEquals(sender, capturedTransaction.getSender());
        assertEquals(receiver, capturedTransaction.getReceiver());
        assertEquals(amount, capturedTransaction.getAmount());
        assertEquals(description, capturedTransaction.getDescription());
    }

    @Test
    void testCreateTransaction_Fail_SameSenderReceiver() {
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                transactionService.createTransaction(sender, sender, "Self transaction", BigDecimal.valueOf(50.00)));

        assertEquals("Impossible d'envoyer de l'argent à soi-même.", exception.getMessage());
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    void testCreateTransaction_Fail_InsufficientFunds() {
        when(userService.getUserById(sender.getId())).thenReturn(sender);
        when(userService.getUserById(receiver.getId())).thenReturn(receiver);
        when(compteService.hasSufficientFunds(sender, BigDecimal.valueOf(200.00))).thenReturn(false);

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                transactionService.createTransaction(sender, receiver, "Test", BigDecimal.valueOf(200.00)));

        assertEquals("Fonds insuffisants pour effectuer cette transaction.", exception.getMessage());

        verify(compteService, never()).debitCompte(any(User.class), any(BigDecimal.class));
        verify(compteService, never()).creditCompte(any(User.class), any(BigDecimal.class));
        verify(transactionRepository, never()).save(any(Transaction.class));
    }
}
