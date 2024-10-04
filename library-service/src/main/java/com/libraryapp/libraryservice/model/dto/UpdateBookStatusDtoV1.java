package com.libraryapp.libraryservice.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;

@Value
@Builder
@Schema(description = "Data transfer object for updating the status of a book.")
public class UpdateBookStatusDtoV1 {

    @PastOrPresent(message = "Borrowed date cannot be in the future")
    @Schema(description = "The date when the book was borrowed. It cannot be in the future.", example = "2023-09-15T10:15:30")
    LocalDateTime borrowedAt;

    @Future(message = "Due date must be in the future")
    @Schema(description = "The due date for returning the book. It must be in the future.", example = "2024-10-15T10:15:30")
    LocalDateTime dueDate;

    @Schema(description = "Indicates if the book is currently available.", example = "true")
    Boolean isFree;
}