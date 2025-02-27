package com.pay_my_buddy.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pay_my_buddy.model.Compte;
import com.pay_my_buddy.model.User;

import java.math.BigInteger;
import java.util.Optional;

public interface CompteRepository extends JpaRepository<Compte, BigInteger> {
    Optional<Compte> findByUser(User user);
}
