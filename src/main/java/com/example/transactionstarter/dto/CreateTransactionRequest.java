package com.example.transactionstarter.dto;

import java.math.BigDecimal;

import com.example.transactionstarter.enums.Currency;
import com.example.transactionstarter.enums.TransactionType;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

//Request DTO for creating a new transaction.
public class CreateTransactionRequest {

    // The ID of the transaction.
    @NotBlank(message = "Transaction ID is required")
    @Pattern(regexp = "^TXN-\\d+$", message = "Transaction ID must start with 'TXN-' followed by numbers (e.g., TXN-001)")
    private String transactionId;

    // The ID of the customer making the transaction.
    @NotBlank(message = "Customer ID is required")
    @Pattern(regexp = "^CUST-\\d+$", message = "Customer ID must start with 'CUST-' followed by numbers (e.g., CUST-101)")
    private String customerId;

    // The amount of the transaction.
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Must be greater than 0")
    private BigDecimal amount;

    // The currency of the transaction.
    @NotNull(message = "Currency is required")
    private Currency currency;

    // The type of transaction.
    @NotNull(message = "Transaction type is required")
    private TransactionType transactionType;

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }
}
