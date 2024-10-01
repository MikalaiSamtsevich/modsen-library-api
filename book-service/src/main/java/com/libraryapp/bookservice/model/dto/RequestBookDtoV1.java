package com.libraryapp.bookservice.model.dto;

import com.libraryapp.bookservice.model.Genre;
import jakarta.validation.constraints.*;
import lombok.Value;

/**
 * DTO for {@link com.libraryapp.bookservice.model.Book}
 */
@Value
public class RequestBookDtoV1 {

    @NotBlank(message = "ISBN must not be blank")
    @Pattern(regexp = "^(97[89])?\\d{9}[0-9X]$", message = "Invalid ISBN format")
    String isbn;

    @NotBlank(message = "Description must not be blank")
    @Size(max = 500, message = "Description must be less than or equal to 500 characters")
    String description;

    @NotBlank(message = "Title must not be blank")
    @Size(max = 255, message = "Title must be less than or equal to 255 characters")
    String title;

    @NotNull(message = "Author ID must not be null")
    @Min(value = 1, message = "Author ID must be greater than 0")
    Long authorId;

    @NotNull(message = "Genre must not be null")
    Genre genre;
}