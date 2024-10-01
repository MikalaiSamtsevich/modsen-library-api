package com.libraryapp.libraryservice.model.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Value;

import java.time.LocalDateTime;

@Value
public class UpdateBookStatusDtoV1 {

    @PastOrPresent(message = "Borrowed date cannot be in the future")
    LocalDateTime borrowedAt;

    @Future(message = "Due date must be in the future")
    LocalDateTime dueDate;

    Boolean isFree;
}
