package com.example.transactionstarter.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.transactionstarter.dto.CreateTransactionRequest;
import com.example.transactionstarter.enums.TransactionStatus;
import com.example.transactionstarter.exceptions.InvalidTransactionException;
import com.example.transactionstarter.exceptions.TransactionNotFoundException;
import com.example.transactionstarter.model.Transaction;
import com.example.transactionstarter.repository.TransactionRepository;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

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

    public Transaction getTransactionsByTransactionId(String transactionId) {
        return transactionRepository.findById(transactionId).orElseThrow(() -> new TransactionNotFoundException("Transaction not found with id: " + transactionId));
    }

    public List<Transaction> getTransactionsByCustomerId(String customerId) {
        List<Transaction> transactions = transactionRepository.findByCustomerId(customerId);
        if (transactions.isEmpty()) {
            throw new TransactionNotFoundException("No transactions found for customer id: " + customerId);
        }
        return transactions;
    }

    public Transaction createTransaction(CreateTransactionRequest request) {

        Transaction transaction = new Transaction();

        transaction.setCustomerId(request.getCustomerId());
        transaction.setAmount(request.getAmount());
        transaction.setCurrency(request.getCurrency());
        transaction.setTransactionType(request.getTransactionType());

        // Set default status
        transaction.setTransactionStatus(TransactionStatus.PENDING);

        String generatedId = UUID.randomUUID().toString();

        transaction.setTransactionId(generatedId);

        return transactionRepository.save(transaction);
    }

    public Transaction updateTransaction(String id, Transaction transaction) {
        Transaction existing = transactionRepository.findById(id)
                .orElseThrow(() -> new TransactionNotFoundException("Transaction not found with id: " + id));

        TransactionStatus newStatus
                = transaction.getTransactionStatus();

        TransactionStatus currentStatus
                = existing.getTransactionStatus();

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
