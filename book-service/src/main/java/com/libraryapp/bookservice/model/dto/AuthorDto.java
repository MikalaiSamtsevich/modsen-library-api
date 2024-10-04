package com.libraryapp.bookservice.model.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Value;

/**
 * DTO for {@link com.libraryapp.bookservice.model.Author}
 */
@Value
@Builder
public class AuthorDto {

    @Min(value = 1, message = "Author ID must be greater than 0")
    @NotNull(message = "ID must not be null")
    Long id;

    @NotBlank(message = "Name must not be blank")
    @Size(max = 100, message = "Name must be less than or equal to 100 characters")
    String name;
}