package com.libraryapp.bookservice.model.dto;


import com.libraryapp.bookservice.model.Genre;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class UpdateBookDtoV1 {

    @Size(max = 500, message = "Description must be less than or equal to 500 characters")
    String description;

    @Size(max = 255, message = "Title must be less than or equal to 255 characters")
    String title;

    @Min(value = 1, message = "Author ID must be greater than 0")
    Long authorId;

    Genre genre;
}
