package com.example.transactionstarter.sample;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.transactionstarter.dto.CreateTransactionRequest;
import com.example.transactionstarter.dto.UpdateTransactionStatusRequest;
import com.example.transactionstarter.model.Transaction;
import com.example.transactionstarter.service.TransactionService;

import jakarta.validation.Valid;

// The main entry point for accessing the transaction service endpoints.
@RestController
@RequestMapping("/transactions")
public class SampleController {

    // Instance variable to hold the transaction service.
    private final TransactionService transactionService;

    // Constructor for dependency injection of the transaction service.
    public SampleController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    // Retrieves all transactions.
    @GetMapping
    public List<Transaction> getAllTransactions() {
        return transactionService.getAllTransactions();
    }

    // Retrieves a transaction by its unique ID.
    @GetMapping("/{transactionId}")
    public Transaction getTransactionsByTransactionId(@PathVariable String transactionId) {
        return transactionService.getTransactionsByTransactionId(transactionId);
    }

    // Updates the status of a transaction.
    @PatchMapping("/{id}/status")
    public Transaction updateTransactionStatus(
            @PathVariable String id,
            @Valid @RequestBody UpdateTransactionStatusRequest request) {

        return transactionService.updateTransactionStatus(id, request.getTransactionStatus());
    }

    // Retrieves all transactions for a specific customer.
    @GetMapping("/customer/{customerId}")
    public List<Transaction> getTransactionsByCustomerId(@PathVariable String customerId) {
        return transactionService.getTransactionsByCustomerId(customerId);
    }

    // Creates a new transaction.
    @PostMapping
    public Transaction createTransaction(
            @Valid @RequestBody CreateTransactionRequest request) {
        return transactionService.createTransaction(request);
    }

    // Updates an existing transaction.
    @PutMapping("/{id}")
    public Transaction updateTransaction(
            @PathVariable String id,
            @Valid @RequestBody Transaction transaction) {
        return transactionService.updateTransaction(id, transaction);
    }

}
