package com.pay_my_buddy.service;

import com.pay_my_buddy.model.User;
import com.pay_my_buddy.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigInteger;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ConnectionServiceTest {

    @Mock
    private UserRepository userRepository;

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
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userRepository.findById(friend.getId())).thenReturn(Optional.of(friend));

        connectionService.addConnection(user.getId(), friend.getId());

        assertTrue(user.getFriends().contains(friend));
        verify(userRepository).save(user);
    }

    @Test
    void testAddConnection_UserNotFound() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, 
            () -> connectionService.addConnection(user.getId(), friend.getId()));

        assertEquals("Utilisateur ou ami non trouvé", exception.getMessage());
    }

    @Test
    void testAddConnection_FriendNotFound() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userRepository.findById(friend.getId())).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, 
            () -> connectionService.addConnection(user.getId(), friend.getId()));

        assertEquals("Utilisateur ou ami non trouvé", exception.getMessage());
    }
}
