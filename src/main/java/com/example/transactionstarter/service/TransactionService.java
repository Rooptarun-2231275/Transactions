package com.example.transactionstarter.service;

import java.util.List;

import org.springframework.stereotype.Service;

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

    public List<Transaction> getTransactionsByCustomerId(String customerId) {
        List<Transaction> transactions = transactionRepository.findByCustomerId(customerId);
        if (transactions.isEmpty()) {
            throw new RuntimeException("Customer not found with id: " + customerId);
        }
        return transactions;
    }

    public Transaction createTransaction(Transaction transaction) {
        if (transaction.getTransactionId() == null || transaction.getTransactionId().isBlank()) {
            long nextId = transactionRepository.count() + 1;
            String generatedId = String.format("TXN-%d", nextId);
            while (transactionRepository.existsById(generatedId)) {
                nextId++;
                generatedId = String.format("TXN-%d", nextId);
            }
            transaction.setTransactionId(generatedId);
        }
        return transactionRepository.save(transaction);
    }

    public Transaction updateTransaction(String id, Transaction transaction) {
        Transaction existing = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found with id: " + id));

        existing.setCustomerId(transaction.getCustomerId());
        existing.setAmount(transaction.getAmount());
        existing.setCurrency(transaction.getCurrency());
        existing.setTransactionType(transaction.getTransactionType());
        existing.setTransactionStatus(transaction.getTransactionStatus());

        return transactionRepository.save(existing);
    }
}
