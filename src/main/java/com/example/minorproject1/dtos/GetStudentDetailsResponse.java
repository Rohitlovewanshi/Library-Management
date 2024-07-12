package com.example.minorproject1.dtos;

import com.example.minorproject1.model.Book;
import com.example.minorproject1.model.Student;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetStudentDetailsResponse {

    private Student student;

    private List<Book> bookList;
}
