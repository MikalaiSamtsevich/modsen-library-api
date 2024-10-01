package com.libraryapp.bookservice.model.dto;

import com.libraryapp.bookservice.model.Genre;
import jakarta.validation.constraints.*;
import lombok.Value;

@Value
public class ResponseBookDtoV1 {

    @NotNull(message = "ID must not be null")
    @Min(value = 1, message = "Book ID must be greater than 0")
    Long id;

    @NotBlank(message = "ISBN must not be blank")
    @Pattern(regexp = "^(97[89])?\\d{9}[0-9X]$", message = "Invalid ISBN format")
    String isbn;

    @NotBlank(message = "Description must not be blank")
    @Size(max = 500, message = "Description must be less than or equal to 500 characters")
    String description;

    @NotBlank(message = "Title must not be blank")
    @Size(max = 255, message = "Title must be less than or equal to 255 characters")
    String title;

    @NotNull(message = "Author information must not be null")
    AuthorDto author;

    @NotNull(message = "Genre must not be null")
    Genre genre;
}
