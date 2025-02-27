package com.pay_my_buddy.controller;

import com.pay_my_buddy.model.Compte;
import com.pay_my_buddy.model.User;
import com.pay_my_buddy.service.CompteService;
import com.pay_my_buddy.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.math.BigDecimal;
import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class CompteControllerTest {

    @Mock
    private CompteService compteService;

    @Mock
    private UserService userService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private CompteController compteController;

    private User user;
    private Compte compte;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setId(BigInteger.ONE);
        user.setEmail("test@example.com");

        compte = new Compte();
        compte.setUser(user);
        compte.setSolde(BigDecimal.ZERO);
    }

    @Test
    void testGetCompteByUser() {
        when(authentication.getName()).thenReturn(user.getEmail());
        when(userService.getUserByEmail(user.getEmail())).thenReturn(user);
        when(compteService.getCompteByUser(user)).thenReturn(compte);

        ResponseEntity<?> response = compteController.getCompteByUser(authentication);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(compte, response.getBody());
    }

    @Test
    void testCreditCompte() {
        when(authentication.getName()).thenReturn(user.getEmail());
        when(userService.getUserByEmail(user.getEmail())).thenReturn(user);
        doNothing().when(compteService).creditCompte(user, BigDecimal.TEN);

        ResponseEntity<String> response = compteController.creditCompte(authentication, BigDecimal.TEN);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Crédit ajouté avec succès.", response.getBody());
    }
    
    @Test
    void testDebitCompte_Success() {
        when(authentication.getName()).thenReturn(user.getEmail());
        when(userService.getUserByEmail(user.getEmail())).thenReturn(user);
        doNothing().when(compteService).debitCompte(user, BigDecimal.TEN);

        ResponseEntity<String> response = compteController.debitCompte(authentication, BigDecimal.TEN);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Débit effectué avec succès.", response.getBody());
    }

    @Test
    void testDebitCompte_Failure_AmountZeroOrNegative() {
        when(authentication.getName()).thenReturn(user.getEmail());
        when(userService.getUserByEmail(user.getEmail())).thenReturn(user);

        ResponseEntity<String> response = compteController.debitCompte(authentication, BigDecimal.ZERO);
        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Le montant doit être supérieur à 0.", response.getBody());

        response = compteController.debitCompte(authentication, BigDecimal.valueOf(-10));
        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Le montant doit être supérieur à 0.", response.getBody());
    }

    @Test
    void testDebitCompte_Failure_InsufficientFunds() {
        when(authentication.getName()).thenReturn(user.getEmail());
        when(userService.getUserByEmail(user.getEmail())).thenReturn(user);

        doThrow(new RuntimeException("Solde insuffisant")).when(compteService).debitCompte(user, BigDecimal.TEN);

        ResponseEntity<String> response = compteController.debitCompte(authentication, BigDecimal.TEN);

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Solde insuffisant.", response.getBody());
    }

    @Test
    void testDebitCompte_Failure_InternalError() {
        when(authentication.getName()).thenReturn(user.getEmail());
        when(userService.getUserByEmail(user.getEmail())).thenReturn(user);

        doThrow(new RuntimeException("Erreur système")).when(compteService).debitCompte(user, BigDecimal.TEN);

        ResponseEntity<String> response = compteController.debitCompte(authentication, BigDecimal.TEN);

        assertEquals(500, response.getStatusCodeValue());
        assertEquals("Une erreur est survenue lors du débit.", response.getBody());
    }

}
