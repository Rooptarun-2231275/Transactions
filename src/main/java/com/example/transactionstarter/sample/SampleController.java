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

@RestController
@RequestMapping("/transactions")
public class SampleController {

    private final TransactionService transactionService;

    public SampleController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping
    public List<Transaction> getAllTransactions() {
        return transactionService.getAllTransactions();
    }

    @GetMapping("/{transactionId}")
    public Transaction getTransactionsByTransactionId(@PathVariable String transactionId) {
        return transactionService.getTransactionsByTransactionId(transactionId);
    }

    @PatchMapping("/{id}/status")
    public Transaction updateTransactionStatus(
            @PathVariable String id,
            @Valid @RequestBody UpdateTransactionStatusRequest request) {

        return transactionService.updateTransactionStatus(id, request.getTransactionStatus());
    }

    @GetMapping("/customer/{customerId}")
    public List<Transaction> getTransactionsByCustomerId(@PathVariable String customerId) {
        return transactionService.getTransactionsByCustomerId(customerId);
    }

    @PostMapping
    public Transaction createTransaction(
            @Valid @RequestBody CreateTransactionRequest request) {
        return transactionService.createTransaction(request);
    }

    @PutMapping("/{id}")
    public Transaction updateTransaction(
            @PathVariable String id,
            @Valid @RequestBody Transaction transaction) {
        return transactionService.updateTransaction(id, transaction);
    }

}
