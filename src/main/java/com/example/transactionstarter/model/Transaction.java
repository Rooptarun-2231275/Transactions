package com.example.transactionstarter.model;

import java.math.BigDecimal;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Column;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import com.example.transactionstarter.enums.Currency;
import com.example.transactionstarter.enums.TransactionStatus;
import com.example.transactionstarter.enums.TransactionType;

@Entity
public class Transaction {

    @Id
    @Column(nullable = false, unique = true)
    private String transactionId;

    @NotBlank(message = "Customer ID is required")
    @Column(nullable = false)
    private String customerId;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Must be greater than 0")
    @Column(nullable = false)
    private BigDecimal amount;

    @NotNull(message = "Currency is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Currency currency;

    @NotNull(message = "Transaction type is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType transactionType;

    @NotNull(message = "Transaction status is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionStatus transactionStatus;


    // No-Args Constructor
    public Transaction() {
    }


    // All-Args Constructor
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


    // Getters

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


    // Setters

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