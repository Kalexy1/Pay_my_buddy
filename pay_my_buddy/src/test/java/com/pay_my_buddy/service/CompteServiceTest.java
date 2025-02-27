package com.pay_my_buddy.service;

import com.pay_my_buddy.model.Compte;
import com.pay_my_buddy.model.User;
import com.pay_my_buddy.repository.CompteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CompteServiceTest {

    @Mock
    private CompteRepository compteRepository;

    @InjectMocks
    private CompteService compteService;

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
    void testCreateCompteForUser_NewUser() {
        when(compteRepository.findByUser(user)).thenReturn(Optional.empty());
        when(compteRepository.save(any(Compte.class))).thenReturn(compte);

        Compte createdCompte = compteService.createCompteForUser(user);

        assertNotNull(createdCompte);
        assertEquals(user, createdCompte.getUser());
        assertEquals(BigDecimal.ZERO, createdCompte.getSolde());
    }

    @Test
    void testCreateCompteForUser_ExistingUser() {
        when(compteRepository.findByUser(user)).thenReturn(Optional.of(compte));

        Compte existingCompte = compteService.createCompteForUser(user);

        assertEquals(compte, existingCompte);
        verify(compteRepository, never()).save(any());
    }

    @Test
    void testCreditCompte_Success() {
        when(compteRepository.findByUser(user)).thenReturn(Optional.of(compte));

        compteService.creditCompte(user, BigDecimal.TEN);

        assertEquals(BigDecimal.TEN, compte.getSolde());
        verify(compteRepository).save(compte);
    }

    @Test
    void testDebitCompte_Success() {
        compte.setSolde(BigDecimal.valueOf(20));
        when(compteRepository.findByUser(user)).thenReturn(Optional.of(compte));

        compteService.debitCompte(user, BigDecimal.TEN);

        assertEquals(BigDecimal.TEN, compte.getSolde());
        verify(compteRepository).save(compte);
    }

    @Test
    void testDebitCompte_InsufficientFunds() {
        when(compteRepository.findByUser(user)).thenReturn(Optional.of(compte));

        assertThrows(RuntimeException.class, () -> compteService.debitCompte(user, BigDecimal.TEN));
    }

    @Test
    void testHasSufficientFunds() {
        compte.setSolde(BigDecimal.valueOf(50));
        when(compteRepository.findByUser(user)).thenReturn(Optional.of(compte));

        assertTrue(compteService.hasSufficientFunds(user, BigDecimal.TEN));
        assertFalse(compteService.hasSufficientFunds(user, BigDecimal.valueOf(100)));
    }
}
