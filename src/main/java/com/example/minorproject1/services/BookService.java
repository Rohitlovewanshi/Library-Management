package com.example.minorproject1.services;

import com.example.minorproject1.dtos.CreateBookRequest;
import com.example.minorproject1.model.Author;
import com.example.minorproject1.model.Book;
import com.example.minorproject1.model.Student;
import com.example.minorproject1.repositories.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService {

    @Autowired
    BookRepository bookRepository;

    @Autowired
    AuthorService authorService;

    public List<Book> getBooksByStudentId(Integer studentId){
        return this.bookRepository.findByStudentId(studentId);
    }

    public Integer create(CreateBookRequest request) {

        Book book = request.mapToBookAndAuthor();
        Author author = book.getAuthor();

        author = this.authorService.getOrCreate(author);
        book.setAuthor(author);

        bookRepository.save(book);

        return book.getId();
    }

    public Book getBookById(Integer bookId) {
        return this.bookRepository.findById(bookId).orElseThrow(null);
    }

    public Book createOrUpdate(Book book){
        return this.bookRepository.save(book);
    }
}
