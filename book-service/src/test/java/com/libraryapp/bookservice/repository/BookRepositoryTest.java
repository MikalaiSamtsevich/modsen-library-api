package com.libraryapp.bookservice.repository;

import com.libraryapp.bookservice.model.Book;
import com.libraryapp.bookservice.model.filter.BookFilter;
import com.libraryapp.bookservice.uti.DataUtils;
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
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;


@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Tag("unit")
@ActiveProfiles("unit-test")
class BookRepositoryTest {

    @Autowired
    BookRepository bookRepository;

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgreSQLContainer =
            new PostgreSQLContainer<>("postgres:16-alpine");

    @Test
    void canEstablishedConnection() {
        assertThat(postgreSQLContainer.isCreated()).isTrue();
        assertThat(postgreSQLContainer.isRunning()).isTrue();
        System.out.println(postgreSQLContainer.getDatabaseName());
    }


    @BeforeEach
    void setUp() {
        bookRepository.save(DataUtils.createBookEntityTransient());
    }

    @AfterEach
    void tearDown() {
        bookRepository.deleteAll();
    }

    @Test
    void shouldReturnBooksWhenIsbnFilterIsApplied() {
        //given
        BookFilter filter = new BookFilter("978479");
        Specification<Book> spec = filter.toSpecification();

        //when
        Page<Book> booksPage = bookRepository.findAll(spec, PageRequest.of(0, 10));

        //then
        assertThat(booksPage).isNotNull();
        assertThat(booksPage.getTotalElements()).isEqualTo(1);
        assertThat(booksPage.getContent()).hasSize(1);
        assertThat(booksPage.getContent().getFirst().getIsbn()).isEqualTo("9784792999998");
    }

}
