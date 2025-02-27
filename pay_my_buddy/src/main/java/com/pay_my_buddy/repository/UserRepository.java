package com.pay_my_buddy.repository;

import com.pay_my_buddy.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigInteger;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, BigInteger> {
    Optional<User> findByEmail(String email);
}
