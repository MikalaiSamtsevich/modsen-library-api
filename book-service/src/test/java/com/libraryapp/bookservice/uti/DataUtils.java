package com.libraryapp.bookservice.uti;

import com.libraryapp.bookservice.model.Author;
import com.libraryapp.bookservice.model.Book;
import com.libraryapp.bookservice.model.Genre;
import com.libraryapp.bookservice.model.dto.AuthorDto;
import com.libraryapp.bookservice.model.dto.RequestBookDtoV1;
import com.libraryapp.bookservice.model.dto.ResponseBookDtoV1;
import com.libraryapp.bookservice.model.dto.UpdateBookDtoV1;

public class DataUtils {

    public static Author createAuthorEntityPersisted() {
        return Author.builder()
                .id(1L)
                .name("Author Name")
                .build();
    }

    public static Author createUnrealAuthorEntityRequestDto() {
        return Author.builder()
                .id(9999L)
                .name("No such name")
                .build();
    }

    public static Author createAuthorEntityTransient() {
        return Author.builder()
                .name("Author Name")
                .build();
    }

    public static AuthorDto createAuthorDto() {
        return AuthorDto.builder()
                .id(1L)
                .name("Author Name")
                .build();
    }

    public static Book createBookEntityPersisted() {
        return Book.builder()
                .id(1L)
                .isbn("9784792999998")
                .description("Book description")
                .title("Book Title")
                .author(createAuthorEntityPersisted())
                .genre(Genre.FICTION)
                .build();
    }

    public static Book createBookEntityTransient() {
        return Book.builder()
                .isbn("9784792999998")
                .description("Book description")
                .title("Book Title")
                .author(createAuthorEntityTransient())
                .genre(Genre.FICTION)
                .build();
    }

    public static RequestBookDtoV1 createRequestBookDto() {
        return RequestBookDtoV1.builder()
                .isbn("9784792999998")
                .description("Book description")
                .title("Book Title")
                .authorId(1L) // ID существующего автора
                .genre(Genre.FICTION)
                .build();
    }

    public static ResponseBookDtoV1 createResponseBookDto() {
        return ResponseBookDtoV1.builder()
                .id(1L)
                .isbn("9784792999998")
                .description("Book description")
                .title("Book Title")
                .author(createAuthorDto()) // Используем метод для получения AuthorDto
                .genre(Genre.FICTION)
                .build();
    }

    public static UpdateBookDtoV1 createUpdateBookDto() {
        return UpdateBookDtoV1.builder()
                .description("Book description")
                .title("Book Title - Revised Edition")
                .authorId(1L) // ID существующего автора
                .genre(Genre.FICTION)
                .build();
    }


}
