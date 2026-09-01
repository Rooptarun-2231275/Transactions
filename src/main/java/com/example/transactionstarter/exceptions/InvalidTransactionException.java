package com.example.transactionstarter.exceptions;

// Exception to be thrown when a transaction is invalid.
public class InvalidTransactionException extends RuntimeException {

    // Constructor that accepts an error message.
    public InvalidTransactionException(String message) {
        super(message);
    }
}
