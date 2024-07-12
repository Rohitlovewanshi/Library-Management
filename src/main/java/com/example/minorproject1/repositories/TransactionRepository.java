package com.example.minorproject1.repositories;

import com.example.minorproject1.model.*;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Integer> {

    Transaction findTopByStudentAndBookAndTransactionTypeAndTransactionStatusOrderByIdDesc(
            Student student, Book book, TransactionType transactionType, TransactionStatus transactionStatus
    );
}
