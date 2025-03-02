package com.pay_my_buddy.service;

import com.pay_my_buddy.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ConnectionServiceTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private ConnectionService connectionService;

    private User user;
    private User friend;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setId(BigInteger.ONE);
        user.setEmail("user@example.com");

        friend = new User();
        friend.setId(BigInteger.TWO);
        friend.setEmail("friend@example.com");
    }

    @Test
    void testAddConnection_Success() {
        when(userService.getUserById(user.getId())).thenReturn(user);
        when(userService.getUserById(friend.getId())).thenReturn(friend);

        connectionService.addConnection(user.getId(), friend.getId());

        assertTrue(user.getFriends().contains(friend));
        assertTrue(friend.getFriends().contains(user));

        verify(userService).saveUser(user);
        verify(userService).saveUser(friend);
    }

    @Test
    void testAddConnection_UserNotFound() {
        when(userService.getUserById(user.getId())).thenThrow(new RuntimeException("Utilisateur non trouvé"));

        Exception exception = assertThrows(RuntimeException.class,
            () -> connectionService.addConnection(user.getId(), friend.getId()));

        assertEquals("Utilisateur non trouvé", exception.getMessage());
    }

    @Test
    void testAddConnection_FriendNotFound() {
        when(userService.getUserById(user.getId())).thenReturn(user);
        when(userService.getUserById(friend.getId())).thenThrow(new RuntimeException("Ami non trouvé"));

        Exception exception = assertThrows(RuntimeException.class,
            () -> connectionService.addConnection(user.getId(), friend.getId()));

        assertEquals("Ami non trouvé", exception.getMessage());
    }
}
