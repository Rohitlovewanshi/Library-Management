package com.example.minorproject1.services;

import com.example.minorproject1.dtos.GetStudentDetailsResponse;
import com.example.minorproject1.model.*;
import com.example.minorproject1.repositories.TransactionRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

@RunWith(MockitoJUnitRunner.class)
public class TransactionServiceTest {

    @InjectMocks
    TransactionService transactionService;

    @Mock
    TransactionRepository transactionRepository;

    @Mock
    StudentService studentService;

    @Mock
    BookService bookService;

    @Test
    public void testCalculateFine_PositiveFine(){

        Book book = Book.builder()
                .id(1)
                .name("History 101")
                .build();

        Student student = Student.builder()
                .id(1)
                .name("Shyam")
                .build();

        Transaction transaction = Transaction.builder()
                .id(1)
                .student(student)
                .book(book)
                .transactionType(TransactionType.ISSUE)
                .transactionStatus(TransactionStatus.SUCCESS)
                .updatedOn(new Date(1713852526000L))
                .build();

        Mockito.when(transactionRepository.findTopByStudentAndBookAndTransactionTypeAndTransactionStatusOrderByIdDesc(
                        Mockito.eq(student),
                        Mockito.eq(book),
                        Mockito.eq(TransactionType.ISSUE),
                        Mockito.eq(TransactionStatus.SUCCESS)))
                .thenReturn(transaction);

        int fine = transactionService.calculateFine(book,student);

        Assert.assertEquals(15,fine);
    }

    @Test
    public void testCalculateFine_NegativeFine(){

        Book book = Book.builder()
                .id(1)
                .name("History 101")
                .build();

        Student student = Student.builder()
                .id(1)
                .name("Shyam")
                .build();

        Transaction transaction = Transaction.builder()
                .id(1)
                .student(student)
                .book(book)
                .transactionType(TransactionType.ISSUE)
                .transactionStatus(TransactionStatus.SUCCESS)
                .updatedOn(new Date(1715753326000L))
                .build();

        Mockito.when(transactionRepository.findTopByStudentAndBookAndTransactionTypeAndTransactionStatusOrderByIdDesc(
                        Mockito.eq(student),
                        Mockito.eq(book),
                        Mockito.eq(TransactionType.ISSUE),
                        Mockito.eq(TransactionStatus.SUCCESS)))
                .thenReturn(transaction);

        int fine = transactionService.calculateFine(book,student);

        Assert.assertEquals(0,fine);
    }

    @Test
    public void testInitiateReturn() throws Exception {

        Student student = Student.builder()
                .id(1)
                .build();

        Book book = Book.builder()
                .id(5)
                .student(student)
                .build();

        GetStudentDetailsResponse getStudentDetailsResponse = GetStudentDetailsResponse.builder()
                .student(student)
                .bookList(new ArrayList<>())
                .build();

        String externalId = UUID.randomUUID().toString();

        Transaction transaction = Transaction.builder()
                .id(1)
                .externalTransactionId(externalId)
                .student(student)
                .book(book)
                .transactionType(TransactionType.ISSUE)
                .transactionStatus(TransactionStatus.SUCCESS)
                .updatedOn(new Date(1715753326000L))
                .build();

//        Transaction transaction = Transaction.builder()
//                .id(1)
//                .externalTransactionId(externalId)
//                .build();

        Mockito.when(studentService.getStudentDetails(1))
                .thenReturn(getStudentDetailsResponse);

        Mockito.when(bookService.getBookById(1)).thenReturn(book);

        Mockito.when(transactionRepository.save(Mockito.any())).thenReturn(transaction);

        Mockito.when(bookService.createOrUpdate(Mockito.any())).thenReturn(book);

        Mockito.when(transactionRepository.save(Mockito.any())).thenReturn(transaction);

        Mockito.when(transactionRepository.findTopByStudentAndBookAndTransactionTypeAndTransactionStatusOrderByIdDesc(
                        Mockito.eq(student),
                        Mockito.eq(book),
                        Mockito.eq(TransactionType.ISSUE),
                        Mockito.eq(TransactionStatus.SUCCESS)))
                .thenReturn(transaction);

        String txnId = transactionService.initiateReturn(1,1);

        Assert.assertEquals(externalId,txnId);
    }

}
