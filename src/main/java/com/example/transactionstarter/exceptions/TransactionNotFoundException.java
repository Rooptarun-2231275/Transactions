package com.example.transactionstarter.exceptions;

// Exception to be thrown when a transaction is not found.
public class TransactionNotFoundException extends RuntimeException {

    // Constructor that accepts an error message.
    public TransactionNotFoundException(String message) {
        super(message);
    }
}
