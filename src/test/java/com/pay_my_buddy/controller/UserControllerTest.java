package com.pay_my_buddy.controller;

import com.pay_my_buddy.model.User;
import com.pay_my_buddy.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private User user;
    private User user1;
    private User user2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setId(BigInteger.ONE);
        user.setEmail("test@example.com");
        user.setUsername("TestUser");
        
        user1 = new User();
        user1.setId(BigInteger.valueOf(1));
        user1.setEmail("user1@example.com");
        user1.setUsername("User1");

        user2 = new User();
        user2.setId(BigInteger.valueOf(2));
        user2.setEmail("user2@example.com");
        user2.setUsername("User2");
    }

    @Test
    void testGetUserByEmail_Success() {
        when(userService.getUserByEmail(user.getEmail())).thenReturn(user);

        ResponseEntity<?> response = userController.getUserByEmail(user.getEmail());

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(user, response.getBody());
    }

    @Test
    void testAddFriend_Success() {
        doNothing().when(userService).addFriend(BigInteger.ONE, "friend@example.com");

        ResponseEntity<?> response = userController.addFriend(BigInteger.ONE, "friend@example.com");

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Ami ajouté avec succès.", response.getBody());
    }

    @Test
    void testRemoveFriend_Success() {
        doNothing().when(userService).removeFriend(BigInteger.ONE, "friend@example.com");

        ResponseEntity<?> response = userController.removeFriend(BigInteger.ONE, "friend@example.com");

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Ami supprimé avec succès.", response.getBody());
    }

    @Test
    void testUpdateUser_Success() {
        doNothing().when(userService).updateUser(BigInteger.ONE, "NewUsername", "new@example.com");
        doNothing().when(userService).updatePassword(BigInteger.ONE, "newPassword");

        ResponseEntity<String> response = userController.updateUser(BigInteger.ONE, "NewUsername", "new@example.com", "newPassword");

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Informations mises à jour avec succès.", response.getBody());
    }
    
    @Test
    void testGetUserFriends_Success_WithFriends() {
        List<User> friends = Arrays.asList(user2);
        when(userService.getUserFriends(user1.getId())).thenReturn(friends);

        ResponseEntity<?> response = userController.getUserFriends(user1.getId());

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(friends, response.getBody());
    }

    @Test
    void testGetUserFriends_Success_NoFriends() {
        when(userService.getUserFriends(user1.getId())).thenReturn(Collections.emptyList());

        ResponseEntity<?> response = userController.getUserFriends(user1.getId());

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Aucun ami trouvé.", response.getBody());
    }

    @Test
    void testGetUserFriends_Failure_UserNotFound() {
        when(userService.getUserFriends(user1.getId())).thenThrow(new RuntimeException("Utilisateur non trouvé"));

        ResponseEntity<?> response = userController.getUserFriends(user1.getId());

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Utilisateur non trouvé", response.getBody());
    }

    @Test
    void testGetUserByEmail_Failure() {
        when(userService.getUserByEmail("unknown@example.com")).thenThrow(new RuntimeException("Utilisateur non trouvé."));

        ResponseEntity<?> response = userController.getUserByEmail("unknown@example.com");

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Utilisateur non trouvé.", response.getBody());
    }

    @Test
    void testAddFriend_Failure() {
        doThrow(new RuntimeException("Impossible d'ajouter cet ami.")).when(userService).addFriend(user.getId(), "friend@example.com");

        ResponseEntity<?> response = userController.addFriend(user.getId(), "friend@example.com");

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Impossible d'ajouter cet ami.", response.getBody());
    }

    @Test
    void testRemoveFriend_Failure() {
        doThrow(new RuntimeException("Cet utilisateur n'est pas un ami.")).when(userService).removeFriend(user.getId(), "friend@example.com");

        ResponseEntity<?> response = userController.removeFriend(user.getId(), "friend@example.com");

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Cet utilisateur n'est pas un ami.", response.getBody());
    }

    @Test
    void testGetUserFriends_Failure() {
        when(userService.getUserFriends(user.getId())).thenThrow(new RuntimeException("Erreur lors de la récupération."));

        ResponseEntity<?> response = userController.getUserFriends(user.getId());

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Erreur lors de la récupération.", response.getBody());
    }

    @Test
    void testUpdateUser_Failure() {
        doThrow(new IllegalArgumentException("Email déjà utilisé.")).when(userService).updateUser(user.getId(), "NewUsername", "existing@example.com");

        ResponseEntity<String> response = userController.updateUser(user.getId(), "NewUsername", "existing@example.com", "newPassword");

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Email déjà utilisé.", response.getBody());
    }

    @Test
    void testUpdateUser_InternalServerError() {
        doThrow(new RuntimeException("Erreur serveur")).when(userService).updateUser(user.getId(), "NewUsername", "new@example.com");

        ResponseEntity<String> response = userController.updateUser(user.getId(), "NewUsername", "new@example.com", "newPassword");

        assertEquals(500, response.getStatusCodeValue());
        assertEquals("Une erreur est survenue.", response.getBody());
    }
    
}
