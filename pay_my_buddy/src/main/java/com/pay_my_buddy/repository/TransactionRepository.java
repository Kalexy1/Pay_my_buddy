package com.pay_my_buddy.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pay_my_buddy.model.Transaction;
import com.pay_my_buddy.model.User;

import java.math.BigInteger;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, BigInteger> {
    List<Transaction> findBySenderOrReceiver(User sender, User receiver);
}
