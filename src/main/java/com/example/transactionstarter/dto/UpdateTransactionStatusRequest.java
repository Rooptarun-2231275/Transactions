package com.example.transactionstarter.dto;

import com.example.transactionstarter.enums.TransactionStatus;

import jakarta.validation.constraints.NotNull;

public class UpdateTransactionStatusRequest {

    @NotNull
    private TransactionStatus transactionStatus;

    public UpdateTransactionStatusRequest() {
    }

    public TransactionStatus getTransactionStatus() {
        return transactionStatus;
    }

    public void setTransactionStatus(TransactionStatus transactionStatus) {
        this.transactionStatus = transactionStatus;
    }
}
