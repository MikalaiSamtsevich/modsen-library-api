package com.libraryapp.bookservice.model.dto;

import com.libraryapp.bookservice.model.Genre;
import jakarta.validation.constraints.Pattern;
import lombok.Value;

/**
 * DTO for {@link com.libraryapp.bookservice.model.Book}
 */
@Value
public class ResponseBookDtoV1 {
    Long id;
    @Pattern(message = "Invalid ISBN format", regexp = "^(97[89])?-?\\d{9}-?([0-9X])$")
    String isbn;
    String description;
    String title;
    AuthorDto author;
    Genre genre;
}