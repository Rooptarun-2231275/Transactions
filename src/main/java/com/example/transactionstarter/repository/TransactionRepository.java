package com.example.transactionstarter.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.transactionstarter.model.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, String> {

}