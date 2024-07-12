package com.example.minorproject1.controller;

import com.example.minorproject1.dtos.CreateBookRequest;
import com.example.minorproject1.model.Book;
import com.example.minorproject1.services.BookService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/books")
public class BookController {

    @Autowired
    BookService bookService;

    @PostMapping()
    public Integer createBook(@Valid @RequestBody CreateBookRequest request){
        return bookService.create(request);
    }

    @GetMapping("/{bookId}")
    public Book getBookById(@PathVariable("bookId") Integer bookId){
        return bookService.getBookById(bookId);
    }
}
