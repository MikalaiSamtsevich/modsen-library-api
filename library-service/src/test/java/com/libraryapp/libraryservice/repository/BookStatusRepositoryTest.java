package com.libraryapp.libraryservice.repository;

import com.libraryapp.libraryservice.model.BookStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;


@Tag("unit")
@DataJpaTest
@ActiveProfiles("unit-test")
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class BookStatusRepositoryTest {

    @Autowired
    BookStatusRepository bookStatusRepository;

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgreSQLContainer =
            new PostgreSQLContainer<>("postgres:16-alpine");

    @BeforeEach
    void setUp() {
        // Создание нескольких записей в таблице book_status
        BookStatus freeBookStatus = new BookStatus();
        freeBookStatus.setBookId(1L);
        freeBookStatus.setIsFree(true);
        freeBookStatus.setBorrowedAt(LocalDateTime.now());
        freeBookStatus.setDueDate(LocalDateTime.now().plusDays(7));
        bookStatusRepository.save(freeBookStatus);

        BookStatus borrowedBookStatus = new BookStatus();
        borrowedBookStatus.setBookId(2L);
        borrowedBookStatus.setIsFree(false);
        borrowedBookStatus.setBorrowedAt(LocalDateTime.now());
        borrowedBookStatus.setDueDate(LocalDateTime.now().plusDays(7));
        bookStatusRepository.save(borrowedBookStatus);
    }

    @AfterEach
    void tearDown() {
        bookStatusRepository.deleteAll();
    }

    @Test
    void shouldReturnAllFreeBooks() {
        // when
        Page<BookStatus> freeBooksPage = bookStatusRepository.findAllFreeBooks(PageRequest.of(0, 10));

        // then
        assertThat(freeBooksPage).isNotNull();
        assertThat(freeBooksPage.getTotalElements()).isEqualTo(1);
        assertThat(freeBooksPage.getContent()).hasSize(1);
        assertThat(freeBooksPage.getContent().get(0).getBookId()).isEqualTo(1L);
    }

    @Test
    void shouldDeleteBookStatusByBookId() {
        // given
        var book = bookStatusRepository.findAll().stream().findFirst();
        Long bookIdToDelete = book.get().getBookId();

        // when
        bookStatusRepository.deleteBookStatusByBookId(bookIdToDelete);

        // then
        assertThat(bookStatusRepository.findById(bookIdToDelete)).isNotPresent();
    }
}