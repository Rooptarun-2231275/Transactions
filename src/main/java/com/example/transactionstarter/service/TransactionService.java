package com.example.transactionstarter.service;

import java.util.List;

import com.example.transactionstarter.dto.CreateTransactionRequest;
import com.example.transactionstarter.enums.TransactionStatus;
import com.example.transactionstarter.model.Transaction;

public interface TransactionService {

    // Retrieves all transactions.
    List<Transaction> getAllTransactions();

    // Retrieves a transaction by its unique ID.
    Transaction getTransactionsByTransactionId(String transactionId);

    // Retrieves all transactions for a specific customer.
    List<Transaction> getTransactionsByCustomerId(String customerId);

    // Creates a new transaction.
    Transaction createTransaction(CreateTransactionRequest request);

    // Updates an existing transaction.
    Transaction updateTransaction(String id, Transaction transaction);

    // Updates the status of a specific transaction.
    Transaction updateTransactionStatus(
            String id,
            TransactionStatus newStatus);
}
