package com.example.minorproject1.services;

import com.example.minorproject1.model.*;
import com.example.minorproject1.repositories.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class TransactionService {

    @Autowired
    TransactionRepository transactionRepository;

    @Autowired
    StudentService studentService;

    @Autowired
    BookService bookService;

    @Value("${students.books.max-allowed}")
    Integer maxAllowedBooks;

//    @Value("${books.return.duration}")
    Integer returnDuration = 15;

//    @Value(("${fine.per-day}"))
    Integer finePerPay = 1;

    public String initiateTransaction(Integer studentId, Integer bookId, TransactionType transactionType) throws Exception {
        switch (transactionType){
            case ISSUE -> {
                return initiateIssuance(studentId, bookId);
            }
            case RETURN -> {
                return initiateReturn(studentId,bookId);
            }
            default -> {
                throw new Exception("Invalid transaction type");
            }
        }
    }

    private String initiateIssuance(Integer studentId, Integer bookId) throws Exception {

        Student student = this.studentService.getStudentDetails(studentId).getStudent();

        if (student == null){
            throw new Exception("Student is not present");
        }

        Book book = this.bookService.getBookById(bookId);

        if (book == null || book.getStudent() != null){
            throw new Exception("Book is not available for issuance");
        }

        List<Book> issuedBooks = student.getBookList();
        if (issuedBooks != null && issuedBooks.size() >= maxAllowedBooks){
            throw new Exception("Student has issued maximum number of books allowed");
        }

        Transaction transaction = Transaction.builder()
                .student(student)
                .book(book)
                .externalTransactionId(UUID.randomUUID().toString())
                .transactionType(TransactionType.ISSUE)
                .transactionStatus(TransactionStatus.PENDING)
                .build();

        transaction = this.transactionRepository.save(transaction);

        try {
            book.setStudent(student);
            book = this.bookService.createOrUpdate(book);

            transaction.setTransactionStatus(TransactionStatus.SUCCESS);
            this.transactionRepository.save(transaction);
        } catch (Exception e){
            transaction.setTransactionStatus(TransactionStatus.FAILED);
            this.transactionRepository.save(transaction);

            if (book.getStudent() != null){
                book.setStudent(null);
                this.bookService.createOrUpdate(book);
            }
        }

        return transaction.getExternalTransactionId();
    }

    public String initiateReturn(Integer studentId, Integer bookId) throws Exception {

        Student student = this.studentService.getStudentDetails(studentId).getStudent();

        if (student == null){
            throw new Exception("Student is not present");
        }

        Book book = this.bookService.getBookById(bookId);

        if (book == null || book.getStudent() == null || book.getStudent().getId() != studentId){
            throw new Exception("Book is not available for return");
        }

        Integer fine = this.calculateFine(book,student);

        Transaction transaction = Transaction.builder()
                .student(student)
                .book(book)
                .externalTransactionId(UUID.randomUUID().toString())
                .transactionType(TransactionType.RETURN)
                .transactionStatus(TransactionStatus.PENDING)
                .fine(fine)
                .build();

        transaction = this.transactionRepository.save(transaction);

        try {
            book.setStudent(null);
            book = this.bookService.createOrUpdate(book);

            transaction.setTransactionStatus(TransactionStatus.SUCCESS);
            this.transactionRepository.save(transaction);
        } catch (Exception e){
            transaction.setTransactionStatus(TransactionStatus.FAILED);
            this.transactionRepository.save(transaction);

            if (book.getStudent() == null){
                book.setStudent(student);
                this.bookService.createOrUpdate(book);
            }
        }

        return transaction.getExternalTransactionId();
    }

    public Integer calculateFine(Book book, Student student) {
        Transaction issuedTxn = this.transactionRepository.findTopByStudentAndBookAndTransactionTypeAndTransactionStatusOrderByIdDesc(student,book, TransactionType.ISSUE, TransactionStatus.SUCCESS);
        Long issuedTimeInMillis = issuedTxn.getUpdatedOn().getTime();
        Long timePassedInMillis = System.currentTimeMillis() - issuedTimeInMillis;
        Long daysPassed = TimeUnit.DAYS.convert(timePassedInMillis, TimeUnit.MILLISECONDS);

        if (daysPassed > returnDuration){
            return (daysPassed.intValue() - returnDuration) * finePerPay;
        }

        return 0;
    }
}
