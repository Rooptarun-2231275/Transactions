package com.example.transactionstarter.dto;

import com.example.transactionstarter.enums.TransactionStatus;

import jakarta.validation.constraints.NotNull;

//Request DTO for updating transaction status.
public class UpdateTransactionStatusRequest {

    // The new status of the transaction.
    @NotNull
    private TransactionStatus transactionStatus;

    // Default constructor for creating new requests.
    public UpdateTransactionStatusRequest() {
    }

    // Getter for the transaction status.
    public TransactionStatus getTransactionStatus() {
        return transactionStatus;
    }

    // Setter for the transaction status.
    public void setTransactionStatus(TransactionStatus transactionStatus) {
        this.transactionStatus = transactionStatus;
    }
}
