package com.example.transactionstarter.exceptions;

// Runtime exception thrown when a duplicate transaction is found.
public class DuplicateTransactionException extends RuntimeException {

    // Constructor that accepts a message.
    public DuplicateTransactionException(String message) {
        super(message);
    }
}
