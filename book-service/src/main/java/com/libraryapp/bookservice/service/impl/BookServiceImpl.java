package com.libraryapp.bookservice.service.impl;

import com.libraryapp.bookservice.exception.BookAlreadyExistsException;
import com.libraryapp.bookservice.kafka.producer.KafkaBookProducer;
import com.libraryapp.bookservice.model.Book;
import com.libraryapp.bookservice.model.dto.RequestBookDtoV1;
import com.libraryapp.bookservice.model.dto.ResponseBookDtoV1;
import com.libraryapp.bookservice.model.dto.UpdateBookDtoV1;
import com.libraryapp.bookservice.model.filter.BookFilter;
import com.libraryapp.bookservice.model.mapper.BookMapper;
import com.libraryapp.bookservice.repository.AuthorRepository;
import com.libraryapp.bookservice.repository.BookRepository;
import com.libraryapp.bookservice.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookMapper bookMapper;
    private final AuthorRepository authorRepository;
    private final BookRepository bookRepository;
    private final KafkaBookProducer kafkaBookProducer;

    @Transactional(readOnly = true)
    @Override
    public Page<ResponseBookDtoV1> getList(BookFilter filter, Pageable pageable) {
        Specification<Book> spec = filter.toSpecification();
        Page<Book> books = bookRepository.findAll(spec, pageable);
        return books.map(bookMapper::toDto);
    }

    @Transactional(readOnly = true)
    @Override
    public ResponseBookDtoV1 getOne(Long id) {
        Optional<Book> bookOptional = bookRepository.findById(id);
        return bookMapper.toDto(bookOptional.orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Book with id `%s` not found".formatted(id))));
    }

    @Transactional(readOnly = true)
    @Override
    public List<ResponseBookDtoV1> getMany(List<Long> ids) {
        List<Book> books = bookRepository.findAllById(ids);
        return books.stream()
                .map(bookMapper::toDto)
                .toList();
    }

    @Transactional
    @Override
    public ResponseBookDtoV1 create(RequestBookDtoV1 dto) {
        Book book = bookMapper.toEntity(dto);
        book.setAuthor(authorRepository.findById(dto.getAuthorId()).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Author with id `%s` not found".formatted(dto.getAuthorId()))));
        try {
            Book resultBook = bookRepository.save(book);
            kafkaBookProducer.sendBookCreatedMessage(resultBook.getId());
            return bookMapper.toDto(resultBook);
        } catch (DataIntegrityViolationException e) {
            throw new BookAlreadyExistsException("A book with ISBN " + book.getIsbn() + " already exists.");
        }
    }

    @Transactional
    @Override
    public ResponseBookDtoV1 patch(Long id, UpdateBookDtoV1 updateBookDto) {
        Book book = bookRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Book with id `%s` not found".formatted(id)));

        updateBookFields(book, updateBookDto);

        Book resultBook = bookRepository.save(book);
        return bookMapper.toDto(resultBook);
    }

    @Transactional
    @Override
    public List<Long> patchMany(List<Long> ids, UpdateBookDtoV1 updateBookDto) {
        Collection<Book> books = bookRepository.findAllById(ids);

        for (Book book : books) {
            updateBookFields(book, updateBookDto);
        }

        List<Book> resultBooks = bookRepository.saveAll(books);
        return resultBooks.stream()
                .map(Book::getId)
                .toList();
    }

    private void updateBookFields(Book book, UpdateBookDtoV1 updateBookDto) {
        if (updateBookDto.getTitle() != null) {
            book.setTitle(updateBookDto.getTitle());
        }
        if (updateBookDto.getDescription() != null) {
            book.setDescription(updateBookDto.getDescription());
        }
        if (updateBookDto.getAuthorId() != null) {
            book.setAuthor(authorRepository.findById(updateBookDto.getAuthorId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                            "Author with id `%s` not found".formatted(updateBookDto.getAuthorId()))));
        }
        if (updateBookDto.getGenre() != null) {
            book.setGenre(updateBookDto.getGenre());
        }
    }

    @Transactional
    @Override
    public ResponseBookDtoV1 delete(Long id) {
        Book book = bookRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Book with id `%s` not found".formatted(id)));
        bookRepository.delete(book);
        kafkaBookProducer.sendBookDeletedMessage(book.getId());
        return bookMapper.toDto(book);
    }

    @Transactional
    @Override
    public void deleteMany(List<Long> ids) {
        List<Book> existingBooks = bookRepository.findAllById(ids);
        List<Long> existingBookIds = existingBooks.stream()
                .map(Book::getId)
                .toList();
        bookRepository.deleteAll(existingBooks);
        for (Long id : existingBookIds) {
            kafkaBookProducer.sendBookDeletedMessage(id);
        }
    }
}
