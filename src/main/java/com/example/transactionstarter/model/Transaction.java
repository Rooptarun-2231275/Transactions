package com.example.transactionstarter.model;

import java.math.BigDecimal;

import com.example.transactionstarter.enums.Currency;
import com.example.transactionstarter.enums.TransactionStatus;
import com.example.transactionstarter.enums.TransactionType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

// The main entity for storing transaction data.
@Entity
public class Transaction {

    // Unique identifier for the transaction.
    @Id
    @Pattern(regexp = "^TXN-\\d+$", message = "Transaction ID must start with 'TXN-' followed by numbers (e.g., TXN-001)")
    @Column(nullable = false, unique = true)
    private String transactionId;

    // Unique identifier for the customer.
    @NotBlank(message = "Customer ID is required")
    @Pattern(regexp = "^CUST-\\d+$", message = "Customer ID must start with 'CUST-' followed by numbers (e.g., CUST-101)")
    @Column(nullable = false)
    private String customerId;

    // The amount of the transaction.
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Must be greater than 0")
    @Column(nullable = false)
    private BigDecimal amount;

    // The currency of the transaction.
    @NotNull(message = "Currency is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Currency currency;

    // The type of transaction (e.g., DEPOSIT, WITHDRAWAL).
    @NotNull(message = "Transaction type is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType transactionType;

    // The status of the transaction.
    @NotNull(message = "Transaction status is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionStatus transactionStatus;

    // Default constructor for JPA.
    public Transaction() {
    }

    // Parameterized constructor for creating new transactions.
    public Transaction(
            String transactionId,
            String customerId,
            BigDecimal amount,
            Currency currency,
            TransactionType transactionType,
            TransactionStatus transactionStatus) {

        this.transactionId = transactionId;
        this.customerId = customerId;
        this.amount = amount;
        this.currency = currency;
        this.transactionType = transactionType;
        this.transactionStatus = transactionStatus;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Currency getCurrency() {
        return currency;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public TransactionStatus getTransactionStatus() {
        return transactionStatus;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public void setTransactionStatus(TransactionStatus transactionStatus) {
        this.transactionStatus = transactionStatus;
    }
}
