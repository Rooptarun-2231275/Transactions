package com.example.transactionstarter.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.transactionstarter.dto.CreateTransactionRequest;
import com.example.transactionstarter.enums.TransactionStatus;
import com.example.transactionstarter.exceptions.DuplicateTransactionException;
import com.example.transactionstarter.exceptions.InvalidTransactionException;
import com.example.transactionstarter.exceptions.TransactionNotFoundException;
import com.example.transactionstarter.model.Transaction;
import com.example.transactionstarter.repository.TransactionRepository;

// Implements the transaction service interface and contains the transaction
// business logic.
@Service
public class TransactionServiceImplementation implements TransactionService {

    // Instance variable to hold the transaction repository.
    private final TransactionRepository transactionRepository;

    // Constructor for dependency injection of the transaction repository.
    public TransactionServiceImplementation(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    // Retrieves all transactions.
    @Override
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    // Checks if a status transition is valid.
    private boolean isValidStatusTransition(
            TransactionStatus currentStatus,
            TransactionStatus newStatus) {

        return switch (currentStatus) {

            case PENDING ->
                newStatus == TransactionStatus.PROCESSING
                || newStatus == TransactionStatus.CANCELLED;

            case PROCESSING ->
                newStatus == TransactionStatus.COMPLETED
                || newStatus == TransactionStatus.FAILED
                || newStatus == TransactionStatus.CANCELLED;

            case COMPLETED, FAILED, CANCELLED ->
                false;
        };
    }

    // Updates the status of a transaction. Validates the status transition before updating.
    @Override
    public Transaction updateTransactionStatus(
            String id,
            TransactionStatus newStatus) {

        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(()
                        -> new TransactionNotFoundException(
                        "Transaction not found with id: " + id
                ));

        TransactionStatus currentStatus
                = transaction.getTransactionStatus();
        // Checks if the new status is valid.If invalid, throws an exception.
        if (!isValidStatusTransition(currentStatus, newStatus)) {
            throw new InvalidTransactionException(
                    "Invalid status transition from "
                    + currentStatus
                    + " to "
                    + newStatus
            );
        }

        transaction.setTransactionStatus(newStatus);

        return transactionRepository.save(transaction);
    }

    // Retrieves a transaction by its unique ID.
    @Override
    public Transaction getTransactionsByTransactionId(String transactionId) {
        return transactionRepository.findById(transactionId).orElseThrow(() -> new TransactionNotFoundException("Transaction not found with id: " + transactionId));
    }

    // Retrieves all transactions for a specific customer.
    @Override
    public List<Transaction> getTransactionsByCustomerId(String customerId) {
        List<Transaction> transactions = transactionRepository.findByCustomerId(customerId);
        if (transactions.isEmpty()) {
            throw new TransactionNotFoundException("No transactions found for customer id: " + customerId);
        }
        return transactions;
    }

    // Creates a new transaction.
    @Override
    public Transaction createTransaction(CreateTransactionRequest request) {

        // Checks if the provided ID already exists.
        if (transactionRepository.existsById(request.getTransactionId())) {
            throw new DuplicateTransactionException(
                    "Transaction already exists with id: "
                    + request.getTransactionId());
        }

        Transaction transaction = new Transaction();

        transaction.setCustomerId(request.getCustomerId());
        transaction.setAmount(request.getAmount());
        transaction.setCurrency(request.getCurrency());
        transaction.setTransactionType(request.getTransactionType());

        // Set default status
        transaction.setTransactionStatus(TransactionStatus.PENDING);

        transaction.setTransactionId(request.getTransactionId());

        return transactionRepository.save(transaction);
    }

    // Updates an existing transaction.
    @Override
    public Transaction updateTransaction(String id, Transaction transaction) {
        Transaction existing = transactionRepository.findById(id)
                .orElseThrow(() -> new TransactionNotFoundException("Transaction not found with id: " + id));

        TransactionStatus newStatus
                = transaction.getTransactionStatus();

        TransactionStatus currentStatus
                = existing.getTransactionStatus();

        if (transaction.getTransactionId() != null && !transaction.getTransactionId().isBlank() && !transaction.getTransactionId().equals(id)) {
            throw new InvalidTransactionException("Transaction id does not match");
        }

        // Checks if the new status is valid.If invalid, throws an exception.
        if (newStatus != null && !newStatus.equals(currentStatus)) {
            if (!isValidStatusTransition(currentStatus, newStatus)) {
                throw new InvalidTransactionException(
                        "Invalid status transition from "
                        + currentStatus
                        + " to "
                        + newStatus
                );
            }
            existing.setTransactionStatus(newStatus);
        }
        existing.setCustomerId(transaction.getCustomerId());
        existing.setAmount(transaction.getAmount());
        existing.setCurrency(transaction.getCurrency());
        existing.setTransactionType(transaction.getTransactionType());

        return transactionRepository.save(existing);
    }
}
