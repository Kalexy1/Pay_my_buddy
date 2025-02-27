package com.pay_my_buddy.service;

import org.springframework.stereotype.Service;

import com.pay_my_buddy.model.User;
import com.pay_my_buddy.repository.UserRepository;

import java.math.BigInteger;
import java.util.Optional;

@Service
public class ConnectionService {

    private final UserRepository userRepository;

    public ConnectionService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void addConnection(BigInteger userId, BigInteger friendId) {
        Optional<User> userOpt = userRepository.findById(userId);
        Optional<User> friendOpt = userRepository.findById(friendId);

        if (userOpt.isPresent() && friendOpt.isPresent()) {
            User user = userOpt.get();
            User friend = friendOpt.get();
            user.getFriends().add(friend);
            userRepository.save(user);
        } else {
            throw new RuntimeException("Utilisateur ou ami non trouvé");
        }
    }
}
