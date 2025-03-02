package com.pay_my_buddy.controller;

import com.pay_my_buddy.model.User;
import com.pay_my_buddy.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class ProfileControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private Authentication authentication;

    @Mock
    private Model model;

    @InjectMocks
    private ProfileController profileController;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setId(BigInteger.ONE);
        user.setEmail("test@example.com");
        user.setUsername("TestUser");
    }

    @Test
    void testShowProfilePage() {
        when(authentication.getName()).thenReturn(user.getEmail());
        when(userService.getUserByEmail(user.getEmail())).thenReturn(user);

        String view = profileController.showProfilePage(authentication, model, null);

        assertEquals("profile", view);
        verify(model).addAttribute("user", user);
    }

    @Test
    void testUpdateProfile_Success() {
        when(authentication.getName()).thenReturn(user.getEmail());
        when(userService.getUserByEmail(user.getEmail())).thenReturn(user);

        String view = profileController.updateProfile(authentication, "NewUsername", "new@example.com", "newPassword123");

        assertEquals("redirect:/profile?message=Mise à jour réussie !", view);
    }

    @Test
    void testUpdateProfile_Failure() {
        when(authentication.getName()).thenReturn(user.getEmail());
        when(userService.getUserByEmail(user.getEmail())).thenReturn(user);
        doThrow(new IllegalArgumentException("Email déjà utilisé")).when(userService)
                .updateUser(user.getId(), "NewUsername", "existing@example.com");

        String view = profileController.updateProfile(authentication, "NewUsername", "existing@example.com", null);

        assertEquals("redirect:/profile?message=Email déjà utilisé", view);
    }
}
