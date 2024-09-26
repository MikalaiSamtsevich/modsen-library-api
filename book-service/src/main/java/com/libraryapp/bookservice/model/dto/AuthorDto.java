package com.libraryapp.bookservice.model.dto;

import lombok.Value;

/**
 * DTO for {@link com.libraryapp.bookservice.model.Author}
 */
@Value
public class AuthorDto {
    Long id;
    String name;
}