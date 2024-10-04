package com.libraryapp.bookservice.service.impl;

import com.libraryapp.bookservice.exception.BookAlreadyExistsException;
import com.libraryapp.bookservice.kafka.producer.KafkaBookProducer;
import com.libraryapp.bookservice.model.Author;
import com.libraryapp.bookservice.model.Book;
import com.libraryapp.bookservice.model.Genre;
import com.libraryapp.bookservice.model.dto.RequestBookDtoV1;
import com.libraryapp.bookservice.model.dto.ResponseBookDtoV1;
import com.libraryapp.bookservice.model.dto.UpdateBookDtoV1;
import com.libraryapp.bookservice.model.filter.BookFilter;
import com.libraryapp.bookservice.model.mapper.BookMapper;
import com.libraryapp.bookservice.repository.AuthorRepository;
import com.libraryapp.bookservice.repository.BookRepository;
import com.libraryapp.bookservice.uti.DataUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Tag("unit")
class BookServiceImplTest {

    @InjectMocks
    BookServiceImpl bookService;

    @Mock
    BookMapper bookMapper;

    @Mock
    KafkaBookProducer kafkaBookProducer;

    @Mock
    BookRepository bookRepository;

    @Mock
    AuthorRepository authorRepository;

    @BeforeEach
    void setUp() {
        bookRepository.save(DataUtils.createBookEntityTransient());
    }


    @Test
    void shouldReturnBooksWhenFilterIsApplied() {
        // given
        BookFilter filter = new BookFilter("978479");
        Pageable pageable = PageRequest.of(0, 10);
        Book book = DataUtils.createBookEntityPersisted();

        Page<Book> booksPage = new PageImpl<>(Collections.singletonList(book));

        when(bookRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(booksPage);
        when(bookMapper.toDto(any(Book.class))).thenReturn(DataUtils.createResponseBookDto());

        // when
        Page<ResponseBookDtoV1> result = bookService.getList(filter, pageable);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst().getIsbn()).isEqualTo(book.getIsbn());
    }

    @Test
    void shouldReturnBookWhenIdExists() {
        // given
        Long existingBookId = 1L;
        Book existingBook = DataUtils.createBookEntityPersisted();

        when(bookRepository.findById(existingBookId)).thenReturn(Optional.of(existingBook));
        when(bookMapper.toDto(existingBook)).thenReturn(DataUtils.createResponseBookDto());

        // when
        ResponseBookDtoV1 result = bookService.getOne(existingBookId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(existingBook.getId());
        assertThat(result.getIsbn()).isEqualTo(existingBook.getIsbn());
        assertThat(result.getTitle()).isEqualTo(existingBook.getTitle());
    }

    @Test
    void shouldThrowExceptionWhenBookNotFound() {
        // given
        Long nonExistingBookId = 999L;
        when(bookRepository.findById(nonExistingBookId)).thenReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> bookService.getOne(nonExistingBookId))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Book with id `999` not found")
                .hasFieldOrPropertyWithValue("status", HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldReturnBooksWhenIdsExist() {
        // given
        List<Long> existingBookIds = List.of(1L, 2L);
        Book book1 = DataUtils.createBookEntityPersisted();
        Book book2 = DataUtils.createBookEntityPersisted();
        book2.setId(2L);

        List<Book> books = List.of(book1, book2);

        when(bookRepository.findAllById(existingBookIds)).thenReturn(books);
        when(bookMapper.toDto(book1)).thenReturn(DataUtils.createResponseBookDto());
        when(bookMapper.toDto(book2)).thenReturn(ResponseBookDtoV1.builder().id(2L).build());

        // when
        List<ResponseBookDtoV1> result = bookService.getMany(existingBookIds);

        // then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getId()).isEqualTo(book1.getId());
        assertThat(result.get(1).getId()).isEqualTo(book2.getId());
    }

    @Test
    void shouldCreateBookWhenValidRequest() {
        // given
        RequestBookDtoV1 requestDto = DataUtils.createRequestBookDto();
        ResponseBookDtoV1 responseBookDto = DataUtils.createResponseBookDto();
        Author author = DataUtils.createAuthorEntityPersisted();
        Book book = DataUtils.createBookEntityPersisted();

        when(bookMapper.toDto(book)).thenReturn(responseBookDto);
        when(bookMapper.toEntity(requestDto)).thenReturn(book);
        when(authorRepository.findById(requestDto.getAuthorId())).thenReturn(Optional.of(author));
        when(bookRepository.save(book)).thenReturn(book);
        doNothing().when(kafkaBookProducer).sendBookCreatedMessage(book.getId());

        // when
        ResponseBookDtoV1 result = bookService.create(requestDto);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getIsbn()).isEqualTo(requestDto.getIsbn());
        verify(bookRepository).save(book);
        verify(kafkaBookProducer).sendBookCreatedMessage(book.getId());
    }

    @Test
    void shouldThrowNotFoundWhenAuthorDoesNotExistWhenCreate() {
        // given
        RequestBookDtoV1 requestDto = DataUtils.createRequestBookDto();

        when(authorRepository.findById(requestDto.getAuthorId())).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> bookService.create(requestDto))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessage("404 NOT_FOUND \"Author with id `1` not found\"");
    }

    @Test
    void shouldThrowBookAlreadyExistsExceptionWhenISBNAlreadyExists() {
        // given
        RequestBookDtoV1 requestDto = DataUtils.createRequestBookDto();
        Author author = DataUtils.createAuthorEntityPersisted();
        when(bookMapper.toEntity(requestDto)).thenReturn(DataUtils.createBookEntityPersisted());
        Book book = bookMapper.toEntity(requestDto);
        book.setAuthor(author);

        when(authorRepository.findById(requestDto.getAuthorId())).thenReturn(Optional.of(author));
        when(bookRepository.save(book)).thenThrow(new DataIntegrityViolationException("Duplicate key exception"));

        // when
        // then
        assertThatThrownBy(() -> bookService.create(requestDto))
                .isInstanceOf(BookAlreadyExistsException.class)
                .hasMessage("A book with ISBN " + book.getIsbn() + " already exists.");
    }

    @Test
    void shouldUpdateBookWhenExistsAndAllFieldsAreProvided() {
        // given
        Long bookId = 1L;
        UpdateBookDtoV1 updateBookDto = DataUtils.createUpdateBookDto();
        Book existingBook = DataUtils.createBookEntityPersisted();
        existingBook.setId(bookId);
        ResponseBookDtoV1 responseBookDto = DataUtils.createResponseBookDto();

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(existingBook));
        when(authorRepository.findById(updateBookDto.getAuthorId())).thenReturn(Optional.of(DataUtils.createAuthorEntityPersisted()));
        when(bookRepository.save(existingBook)).thenReturn(existingBook);
        when(bookMapper.toDto(existingBook)).thenReturn(responseBookDto);

        // when
        ResponseBookDtoV1 result = bookService.patch(bookId, updateBookDto);

        // then
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(responseBookDto);
        assertThat(existingBook.getTitle()).isEqualTo(updateBookDto.getTitle());
        assertThat(existingBook.getDescription()).isEqualTo(updateBookDto.getDescription());
        assertThat(existingBook.getGenre()).isEqualTo(updateBookDto.getGenre());
        assertThat(existingBook.getAuthor()).isNotNull();
        assertThat(existingBook.getAuthor().getId()).isEqualTo(updateBookDto.getAuthorId());
        verify(bookRepository).findById(bookId);
        verify(authorRepository).findById(updateBookDto.getAuthorId());
        verify(bookRepository).save(existingBook);
        verify(bookMapper).toDto(existingBook);
    }

    @Test
    void shouldThrowNotFoundWhenBookDoesNotExist() {
        // given
        Long bookId = 1L;
        UpdateBookDtoV1 updateBookDto = DataUtils.createUpdateBookDto();

        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> bookService.patch(bookId, updateBookDto))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessage("404 NOT_FOUND \"Book with id `1` not found\"");
    }

    @Test
    void shouldThrowNotFoundWhenAuthorDoesNotExistWhenUpdate() {
        // given
        Long bookId = 1L;
        UpdateBookDtoV1 updateBookDto = UpdateBookDtoV1.builder()
                .description("Book description")
                .title("Book Title")
                .authorId(999L)
                .genre(Genre.FICTION)
                .build();

        Book existingBook = DataUtils.createBookEntityPersisted();
        existingBook.setId(bookId);

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(existingBook));
        when(authorRepository.findById(updateBookDto.getAuthorId())).thenReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> bookService.patch(bookId, updateBookDto))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessage("404 NOT_FOUND \"Author with id `999` not found\"");
    }

    @Test
    void shouldUpdateManyBooksWhenExists() {
        // given
        List<Long> bookIds = List.of(1L, 2L);
        UpdateBookDtoV1 updateBookDto = DataUtils.createUpdateBookDto();
        Book existingBook1 = DataUtils.createBookEntityPersisted();
        existingBook1.setId(1L);
        Book existingBook2 = DataUtils.createBookEntityPersisted();
        existingBook2.setId(2L);
        existingBook2.setTitle("Old Title");

        List<Book> existingBooks = List.of(existingBook1, existingBook2);
        List<Book> expectedUpdatedBooks = List.of(existingBook1, existingBook2);

        when(bookRepository.findAllById(bookIds)).thenReturn(existingBooks);
        when(bookRepository.saveAll(existingBooks)).thenReturn(expectedUpdatedBooks);
        when(authorRepository.findById(updateBookDto.getAuthorId())).thenReturn(Optional.of(DataUtils.createAuthorEntityPersisted()));

        // when
        List<Long> result = bookService.patchMany(bookIds, updateBookDto);

        // then
        assertThat(result).hasSize(2);
        assertThat(result).containsExactlyInAnyOrder(1L, 2L);
        assertThat(existingBook1.getTitle()).isEqualTo(updateBookDto.getTitle());
        assertThat(existingBook1.getDescription()).isEqualTo(updateBookDto.getDescription());
        assertThat(existingBook1.getGenre()).isEqualTo(updateBookDto.getGenre());
        assertThat(existingBook2.getTitle()).isEqualTo(updateBookDto.getTitle());
        assertThat(existingBook2.getDescription()).isEqualTo(updateBookDto.getDescription());
        assertThat(existingBook2.getGenre()).isEqualTo(updateBookDto.getGenre());

        verify(bookRepository).findAllById(bookIds);
        verify(bookRepository).saveAll(existingBooks);
    }

    @Test
    void shouldReturnEmptyListWhenNoBooksFoundWhenUpdateMany() {
        // given
        List<Long> bookIds = List.of(1L, 2L);
        UpdateBookDtoV1 updateBookDto = DataUtils.createUpdateBookDto();

        when(bookRepository.findAllById(bookIds)).thenReturn(Collections.emptyList());

        // when
        List<Long> result = bookService.patchMany(bookIds, updateBookDto);

        // then
        assertThat(result).isEmpty();
        verify(bookRepository).findAllById(bookIds);
    }


    @Test
    void shouldDeleteBookWhenExists() {
        // given
        Long bookId = 1L;
        Book book = DataUtils.createBookEntityPersisted();
        book.setId(bookId);

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        doNothing().when(bookRepository).delete(book);
        doNothing().when(kafkaBookProducer).sendBookDeletedMessage(bookId);
        when(bookMapper.toDto(book)).thenReturn(DataUtils.createResponseBookDto());

        // when
        ResponseBookDtoV1 result = bookService.delete(bookId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(book.getId());

        verify(bookRepository).findById(bookId);
        verify(bookRepository).delete(book);
        verify(kafkaBookProducer).sendBookDeletedMessage(bookId);
    }

    @Test
    void shouldThrowExceptionWhenBookNotFoundWhenDelete() {
        // given
        Long bookId = 1L;

        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        // when
        // then
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> bookService.delete(bookId));
        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(exception.getReason()).isEqualTo("Book with id `1` not found");

        verify(bookRepository).findById(bookId);
        verify(bookRepository, never()).delete(any(Book.class));
        verify(kafkaBookProducer, never()).sendBookDeletedMessage(any());
    }

    @Test
    void shouldDeleteManyBooksWhenExists() {
        // given
        List<Long> bookIds = Arrays.asList(1L, 2L, 3L);
        Book book1 = DataUtils.createBookEntityPersisted();
        book1.setId(1L);
        Book book2 = DataUtils.createBookEntityPersisted();
        book2.setId(2L);
        Book book3 = DataUtils.createBookEntityPersisted();
        book3.setId(3L);
        List<Book> existingBooks = Arrays.asList(book1, book2, book3);

        when(bookRepository.findAllById(bookIds)).thenReturn(existingBooks);
        doNothing().when(bookRepository).deleteAll(existingBooks);
        doNothing().when(kafkaBookProducer).sendBookDeletedMessage(anyLong());

        // when
        bookService.deleteMany(bookIds);

        // then
        verify(bookRepository).findAllById(bookIds);
        verify(bookRepository).deleteAll(existingBooks);
        for (Long id : bookIds) {
            verify(kafkaBookProducer).sendBookDeletedMessage(id);
        }
    }
}