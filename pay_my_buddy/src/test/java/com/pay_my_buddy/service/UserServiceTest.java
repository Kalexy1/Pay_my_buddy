package com.pay_my_buddy.service;

import com.pay_my_buddy.exception.ResourceNotFoundException;
import com.pay_my_buddy.model.Compte;
import com.pay_my_buddy.model.User;
import com.pay_my_buddy.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private CompteService compteService;

    @InjectMocks
    private UserService userService;

    private User user;
    private User friend;
    private Compte compte;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setId(BigInteger.ONE);
        user.setEmail("test@example.com");
        user.setPassword("password123");
        friend = new User();
        friend.setId(BigInteger.TWO);
        friend.setEmail("friend@example.com");
        friend.setFriends(new HashSet<>());
        compte = new Compte();
        compte.setUser(user);
    }

    @Test
    void testRegisterUser_Success() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(user.getPassword())).thenReturn("hashedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(compteService.createCompteForUser(user)).thenReturn(compte);

        User result = userService.registerUser(user);

        assertNotNull(result);
        assertEquals(user.getEmail(), result.getEmail());
        verify(userRepository).save(any(User.class));
        verify(compteService).createCompteForUser(user);
    }

    @Test
    void testRegisterUser_EmailAlreadyExists() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> userService.registerUser(user));

        assertEquals("Cet email est déjà utilisé.", exception.getMessage());
    }

    @Test
    void testAuthenticate_Success() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password123", user.getPassword())).thenReturn(true);

        User result = userService.authenticate(user.getEmail(), "password123");

        assertNotNull(result);
        assertEquals(user.getEmail(), result.getEmail());
    }

    @Test
    void testAuthenticate_WrongPassword() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongpassword", user.getPassword())).thenReturn(false);

        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> userService.authenticate(user.getEmail(), "wrongpassword"));

        assertEquals("Email ou mot de passe incorrect.", exception.getMessage());
    }

    @Test
    void testAuthenticate_UserNotFound() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());

        Exception exception = assertThrows(ResourceNotFoundException.class,
                () -> userService.authenticate(user.getEmail(), "password123"));

        assertEquals("Utilisateur non trouvé avec l'email : " + user.getEmail(), exception.getMessage());
    }

    @Test
    void testGetUserByEmail_Success() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        User result = userService.getUserByEmail(user.getEmail());

        assertNotNull(result);
        assertEquals(user.getEmail(), result.getEmail());
    }

    @Test
    void testGetUserByEmail_NotFound() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());

        Exception exception = assertThrows(ResourceNotFoundException.class,
                () -> userService.getUserByEmail(user.getEmail()));

        assertEquals("Utilisateur non trouvé avec l'email : " + user.getEmail(), exception.getMessage());
    }

    @Test
    void testUpdateUser_Success() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userRepository.findByEmail("new@example.com")).thenReturn(Optional.empty());

        userService.updateUser(user.getId(), "NewUsername", "new@example.com");

        assertEquals("NewUsername", user.getUsername());
        assertEquals("new@example.com", user.getEmail());
    }

    @Test
    void testUpdatePassword_Success() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("newpassword")).thenReturn("hashedNewPassword");

        userService.updatePassword(user.getId(), "newpassword");

        assertEquals("hashedNewPassword", user.getPassword());
    }
    
    @Test
    void testAddFriend_Success() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userRepository.findByEmail(friend.getEmail())).thenReturn(Optional.of(friend));

        userService.addFriend(user.getId(), friend.getEmail());

        assertTrue(user.getFriends().contains(friend));
        assertTrue(friend.getFriends().contains(user));

        verify(userRepository, times(1)).save(user);
        verify(userRepository, times(1)).save(friend);
    }

    @Test
    void testAddFriend_AlreadyFriends() {
        user.getFriends().add(friend);
        friend.getFriends().add(user);

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userRepository.findByEmail(friend.getEmail())).thenReturn(Optional.of(friend));

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                userService.addFriend(user.getId(), friend.getEmail()));

        assertEquals("Cet utilisateur est déjà dans votre liste d'amis.", exception.getMessage());
    }

    @Test
    void testAddFriend_SelfAddition() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                userService.addFriend(user.getId(), user.getEmail()));

        assertEquals("Vous ne pouvez pas vous ajouter vous-même.", exception.getMessage());
    }

    @Test
    void testAddFriend_UserNotFound() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        Exception exception = assertThrows(ResourceNotFoundException.class, () ->
                userService.addFriend(user.getId(), friend.getEmail()));

        assertEquals("Utilisateur non trouvé avec l'ID : 1", exception.getMessage());
    }

    @Test
    void testAddFriend_FriendNotFound() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userRepository.findByEmail(friend.getEmail())).thenReturn(Optional.empty());

        Exception exception = assertThrows(ResourceNotFoundException.class, () ->
                userService.addFriend(user.getId(), friend.getEmail()));

        assertEquals("Utilisateur non trouvé avec l'email : friend@example.com", exception.getMessage());
    }

    @Test
    void testRemoveFriend_Success() {
        user.getFriends().add(friend);
        friend.getFriends().add(user);

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userRepository.findByEmail(friend.getEmail())).thenReturn(Optional.of(friend));

        userService.removeFriend(user.getId(), friend.getEmail());

        assertFalse(user.getFriends().contains(friend));
        assertFalse(friend.getFriends().contains(user));

        verify(userRepository, times(1)).save(user);
        verify(userRepository, times(1)).save(friend);
    }

    @Test
    void testRemoveFriend_NotInFriendsList() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userRepository.findByEmail(friend.getEmail())).thenReturn(Optional.of(friend));

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                userService.removeFriend(user.getId(), friend.getEmail()));

        assertEquals("Cet utilisateur n'est pas dans votre liste d'amis.", exception.getMessage());
    }

    @Test
    void testGetUserFriends_Success() {
        user.getFriends().add(friend);

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        var friendsList = userService.getUserFriends(user.getId());

        assertEquals(1, friendsList.size());
        assertTrue(friendsList.contains(friend));
    }

    @Test
    void testGetUserFriends_UserNotFound() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        Exception exception = assertThrows(ResourceNotFoundException.class, () ->
                userService.getUserFriends(user.getId()));

        assertEquals("Utilisateur non trouvé avec l'ID : 1", exception.getMessage());
    }
}
