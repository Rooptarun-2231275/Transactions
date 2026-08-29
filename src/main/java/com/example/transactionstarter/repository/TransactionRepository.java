package com.example.transactionstarter.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.transactionstarter.model.Transaction;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, String> {
    List<Transaction> findByCustomerId(String customerId);
}

