package com.libraryapp.bookservice.model.dto;

import com.libraryapp.bookservice.model.Genre;
import jakarta.validation.constraints.Pattern;
import lombok.Value;

/**
 * DTO for {@link com.libraryapp.bookservice.model.Book}
 */
@Value
public class RequestBookDtoV1 {
    @Pattern(regexp = "^(97[89])?\\d{9}[0-9X]$", message = "Invalid ISBN format")
    String isbn;
    String description;
    String title;
    Long authorId;
    Genre genre;
}