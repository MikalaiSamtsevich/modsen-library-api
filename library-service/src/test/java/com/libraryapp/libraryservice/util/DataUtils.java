package com.libraryapp.libraryservice.util;

import com.libraryapp.libraryservice.model.BookStatus;
import com.libraryapp.libraryservice.model.dto.ResponseBookStatusDtoV1;
import com.libraryapp.libraryservice.model.dto.UpdateBookStatusDtoV1;

import java.time.LocalDateTime;

public class DataUtils {

    public static BookStatus createBookStatusEntityPersisted() {
        return BookStatus.builder()
                .id(1L)
                .bookId(101L)
                .borrowedAt(LocalDateTime.now().minusDays(1))
                .dueDate(LocalDateTime.now().plusDays(5))
                .isFree(true)
                .build();
    }

    public static BookStatus createNotFreeBookStatusEntityPersisted() {
        return BookStatus.builder()
                .id(1L)
                .bookId(101L)
                .borrowedAt(LocalDateTime.now().minusDays(1))
                .dueDate(LocalDateTime.now().plusDays(5))
                .isFree(false)
                .build();
    }

    public static BookStatus createBookStatusEntityTransient() {
        return BookStatus.builder()
                .bookId(101L)
                .borrowedAt(LocalDateTime.now().minusDays(1))
                .dueDate(LocalDateTime.now().plusDays(5))
                .isFree(true)
                .build();
    }

    public static ResponseBookStatusDtoV1 createResponseBookStatusDto() {
        return ResponseBookStatusDtoV1.builder()
                .id(1L)
                .bookId(101L)
                .borrowedAt(LocalDateTime.now().minusDays(1))
                .dueDate(LocalDateTime.now().plusDays(5))
                .isFree(true)
                .build();
    }

    public static UpdateBookStatusDtoV1 createUpdateBookStatusDto() {
        return UpdateBookStatusDtoV1.builder()
                .borrowedAt(LocalDateTime.now().minusDays(1))
                .dueDate(LocalDateTime.now().plusDays(5))
                .isFree(true)
                .build();
    }

    public static ResponseBookStatusDtoV1 createUnrealResponseBookStatusDto() {
        return ResponseBookStatusDtoV1.builder()
                .id(9999L)
                .bookId(9999L)
                .borrowedAt(LocalDateTime.now().minusDays(1))
                .dueDate(LocalDateTime.now().plusDays(5))
                .isFree(false)
                .build();
    }
}