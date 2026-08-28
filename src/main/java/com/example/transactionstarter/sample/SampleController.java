package com.example.transactionstarter.sample;

import com.example.transactionstarter.model.Transaction;
import com.example.transactionstarter.repository.TransactionRepository;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/transactions")
public class SampleController {

    private final TransactionRepository transactionRepository;

    public SampleController(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @GetMapping
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    @GetMapping("/{id}")
    public Transaction getTransactionById(@PathVariable Long id) {
        return transactionRepository.findById(String.valueOf(id))
                .orElse(null);
    }

    @PostMapping
    public Transaction createTransaction(@Valid @RequestBody Transaction transaction) {
        return transactionRepository.save(transaction);
    }

    @PutMapping("/{id}")
    public Transaction updateTransaction(
            @PathVariable String id,
            @Valid @RequestBody Transaction transaction) {

        transaction.setTransactionId(id);
        return transactionRepository.save(transaction);
    }

}