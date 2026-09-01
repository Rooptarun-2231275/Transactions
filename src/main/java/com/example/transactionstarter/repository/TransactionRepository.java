package com.example.transactionstarter.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.transactionstarter.model.Transaction;

// Interface for accessing the transaction database.
public interface TransactionRepository extends JpaRepository<Transaction, String> {

    // Retrieves all transactions for a specific customer.
    List<Transaction> findByCustomerId(String customerId);
}
