package com.libraryapp.libraryservice.model.dto;

import lombok.Value;

import java.time.LocalDateTime;

/**
 * DTO for {@link com.libraryapp.libraryservice.model.BookStatus}
 */
@Value
public class BookStatusDto {
    Long id;
    Long bookId;
    LocalDateTime borrowedAt;
    LocalDateTime dueDate;
    Boolean isFree;
}