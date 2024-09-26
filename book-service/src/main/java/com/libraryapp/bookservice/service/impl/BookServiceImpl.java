package com.libraryapp.bookservice.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.libraryapp.bookservice.model.Book;
import com.libraryapp.bookservice.model.dto.RequestBookDtoV1;
import com.libraryapp.bookservice.model.dto.ResponseBookDtoV1;
import com.libraryapp.bookservice.model.filter.BookFilter;
import com.libraryapp.bookservice.model.mapper.BookMapper;
import com.libraryapp.bookservice.repository.AuthorRepository;
import com.libraryapp.bookservice.repository.BookRepository;
import com.libraryapp.bookservice.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookMapper bookMapper;
    private final AuthorRepository authorRepository;
    private final BookRepository bookRepository;
    private final ObjectMapper objectMapper;

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
        Book resultBook = bookRepository.save(book);
        return bookMapper.toDto(resultBook);
    }

    @Transactional
    @Override
    public ResponseBookDtoV1 patch(Long id, JsonNode patchNode) throws IOException {
        Book book = bookRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Book with id `%s` not found".formatted(id)));

        RequestBookDtoV1 requestBookDtoV1 = bookMapper.toDtoWithoutId(book);
        objectMapper.readerForUpdating(requestBookDtoV1).readValue(patchNode);
        bookMapper.partialUpdate(requestBookDtoV1, book);

        Book resultBook = bookRepository.save(book);
        return bookMapper.toDto(resultBook);
    }

    @Transactional
    @Override
    public List<Long> patchMany(List<Long> ids, JsonNode patchNode) throws IOException {
        Collection<Book> books = bookRepository.findAllById(ids);

        for (Book book : books) {
            RequestBookDtoV1 requestBookDtoV1 = bookMapper.toDtoWithoutId(book);
            objectMapper.readerForUpdating(requestBookDtoV1).readValue(patchNode);
            bookMapper.partialUpdate(requestBookDtoV1, book);
        }

        List<Book> resultBooks = bookRepository.saveAll(books);
        return resultBooks.stream()
                .map(Book::getId)
                .toList();
    }

    @Transactional
    @Override
    public ResponseBookDtoV1 delete(Long id) {
        Book book = bookRepository.findById(id).orElse(null);
        if (book != null) {
            bookRepository.delete(book);
        }
        return bookMapper.toDto(book);
    }

    @Transactional
    @Override
    public void deleteMany(List<Long> ids) {
        bookRepository.deleteAllById(ids);
    }
}
