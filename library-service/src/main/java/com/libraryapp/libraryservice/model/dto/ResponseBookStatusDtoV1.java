package com.libraryapp.libraryservice.model.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Value;

import java.time.LocalDateTime;

/**
 * DTO for {@link com.libraryapp.libraryservice.model.BookStatus}
 */
@Value
public class ResponseBookStatusDtoV1 {

    @NotNull(message = "ID must not be null")
    @Min(value = 1, message = "ID must be greater than 0")
    Long id;

    @NotNull(message = "Book ID must not be null")
    @Min(value = 1, message = "Book ID must be greater than 0")
    Long bookId;

    @NotNull(message = "Borrowed date must not be null")
    @PastOrPresent(message = "Borrowed date cannot be in the future")
    LocalDateTime borrowedAt;

    @NotNull(message = "Due date must not be null")
    @Future(message = "Due date must be in the future")
    LocalDateTime dueDate;

    @NotNull(message = "Free status must not be null")
    Boolean isFree;
}
