package com.pay_my_buddy.controller;

import com.pay_my_buddy.model.User;
import com.pay_my_buddy.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class AuthControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private Model model;

    @InjectMocks
    private AuthController authController;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setId(BigInteger.ONE);
        user.setEmail("test@example.com");
        user.setPassword("password");
    }

    @Test
    void testHome() {
        String view = authController.home();
        assertEquals("redirect:/login", view);
    }

    @Test
    void testShowLoginPage_NoError() {
        String view = authController.showLoginPage(null, model);
        assertEquals("login", view);
        verify(model, never()).addAttribute(eq("error"), any());
    }

    @Test
    void testShowLoginPage_WithError() {
        String view = authController.showLoginPage("error", model);
        assertEquals("login", view);
        verify(model).addAttribute("error", "Identifiants invalides. Veuillez réessayer.");
    }

    @Test
    void testLogin_Success() {
        when(userService.authenticate("test@example.com", "password")).thenReturn(user);

        String view = authController.login("test@example.com", "password", model);
        assertEquals("redirect:/transfer?user=1", view);
    }

    @Test
    void testLogin_Failure() {
        when(userService.authenticate("test@example.com", "password")).thenReturn(null);

        String view = authController.login("test@example.com", "password", model);
        assertEquals("login", view);
        verify(model).addAttribute("error", "Email ou mot de passe incorrect.");
    }

    @Test
    void testShowRegisterPage() {
        String view = authController.showRegisterPage();
        assertEquals("register", view);
    }

    @Test
    void testRegister_Success() {
        when(userService.registerUser(any(User.class))).thenReturn(user);

        String view = authController.register(user, model);
        assertEquals("redirect:/login", view);
    }

    @Test
    void testRegister_Failure() {
        doThrow(new IllegalArgumentException("Cet email est déjà utilisé.")).when(userService).registerUser(any(User.class));

        String view = authController.register(user, model);
        assertEquals("register", view);
        verify(model).addAttribute("error", "Cet email est déjà utilisé.");
    }
}
