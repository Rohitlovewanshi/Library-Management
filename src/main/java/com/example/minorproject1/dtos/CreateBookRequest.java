package com.example.minorproject1.dtos;

import com.example.minorproject1.model.Author;
import com.example.minorproject1.model.Book;
import com.example.minorproject1.model.Genre;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateBookRequest {

    private String bookName;

    private Genre genre;

    private String authorName;

    @NotBlank
    private String authorEmail;

    private String authorCountry;

    public Book mapToBookAndAuthor(){
        return Book.builder()
                .name(this.bookName)
                .genre(this.genre)
                .author(
                        Author.builder()
                                .name(this.authorName)
                                .email(this.authorEmail)
                                .build()
                )
                .build();
    }
}
